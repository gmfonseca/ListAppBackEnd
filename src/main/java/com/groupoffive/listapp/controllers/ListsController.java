package com.groupoffive.listapp.controllers;

import com.groupoffive.listapp.exceptions.*;
import com.groupoffive.listapp.models.*;
import com.groupoffive.listapp.util.NotificationService;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;

public class ListsController {

    private EntityManager entityManager;
    private NotificationService notificator;

    public ListsController(EntityManager entityManager, NotificationService notificator) {
        this.entityManager = entityManager;
        this.notificator   = notificator;
    }

    public ListaDeCompras createList(String listName, int groupId) throws GroupNotFoundException {
        GrupoDeUsuarios grupo = entityManager.find(GrupoDeUsuarios.class, groupId);

        if (null == grupo) throw new GroupNotFoundException();
        ListaDeCompras lista = new ListaDeCompras(listName, grupo);
        grupo.getListasDeCompras().add(lista);

        entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        return lista;
    }

    /**
     * Retorna os produtos pertencentes a uma lista.
     * @param listId id da lista a ser buscada
     * @return devolve os produtos da lista
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public Set<Produto> getListProducts(int listId) throws ListNotFoundException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) throw new ListNotFoundException();

        return lista.getProdutos();
    }

    /**
     *
     */
    public Set<ComentarioLista> getComments(int listId) throws ListNotFoundException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if(null == lista) throw new ListNotFoundException();

        return lista.getComentarios();
    }

    /**
     * Retorna as categories pertencentes a uma lista.
     * @param listId id da lista a ser buscada
     * @return devolve as categorias da lista
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public Set<Categoria> getListCategories(int listId) throws ListNotFoundException {
        Set<Produto> produtos;
        Set<Categoria> categorias = new HashSet<>();
        ListaDeCompras lista  = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) throw new ListNotFoundException();

        produtos = lista.getProdutos();

        for (Produto produto : produtos) {
            Categoria categoria             = produto.getCategoria();

            /* Selecionando na categoria somente os produtos que pertencem à lista */
            Set<Produto> produtosCategoria  = categoria.getProdutos();
            Set<Produto> produtosCategoria2 = new HashSet<>();
            for (Produto p : produtosCategoria)
                if (p.belongsToList(lista, this.entityManager)) produtosCategoria2.add(p);
            categoria.setProdutos(produtosCategoria2);

            categorias.add(categoria);
        }

        return categorias;
    }

    public ListaDeCompras addProduct(int listId, int productId) throws ListNotFoundException, ProductNotFoundException, ProductAlreadyInListException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);
        Produto produto = entityManager.find(Produto.class, productId);

        if (null == lista) throw new ListNotFoundException();
        if(null == produto) throw new ProductNotFoundException();
        if(lista.getProdutos().contains(produto)) throw new ProductAlreadyInListException();

        lista.getProdutos().add(produto);

        entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        return lista;
    }

    public ListaDeCompras removeProduct(int listId, int productId) throws ListNotFoundException, ProductNotFoundException, ProductDoesNotInListException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);
        Produto produto = entityManager.find(Produto.class, productId);

        if (null == lista) throw new ListNotFoundException();
        if(null == produto) throw new ProductNotFoundException();
        if(!lista.getProdutos().contains(produto)) throw new ProductDoesNotInListException();

        lista.getProdutos().remove(produto);

        entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        return lista;
    }

    /**
     * Adiciona um comentario em uma lista específica por um usuário
     * @param listId id da lista a ser atualizada
     * @param userId id do usuario que deseja inserir um comentario
     * @param comment comentario a ser inserido em uma lista
     *
     * @throws ListNotFoundException caso lista com este id não seja encontrada
     * @throws UserNotFoundException caso o usuario solicitado nao esteja cadastrado
     * @throws EmptyCommentException caso o comentario esteja vazio
     * @throws UserNotInGroupException caso o usuario nao esteja inserido no respectivo grupo
     */
    public Set<ComentarioLista> addComment(int listId, int userId, String comment)throws ListNotFoundException, UserNotFoundException, EmptyCommentException, UserNotInGroupException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);
        Usuario user = entityManager.find(Usuario.class, userId);

        if (null == lista) throw new ListNotFoundException();
        if (null == user) throw new UserNotFoundException();
        if (fieldIsEmpty(comment)) throw new EmptyCommentException();
        if (!lista.getGrupoDeUsuarios().containsUser(user)) throw new UserNotInGroupException();

        Comentario comentario = new Comentario(comment);

        ComentarioLista cl = new ComentarioLista(user, lista, comentario);

        user.getComentarios().add(cl);
        lista.getComentarios().add(cl);
        comentario.getListas().add(cl);

        entityManager.getTransaction().begin();
        entityManager.persist(comentario);
        entityManager.persist(cl);
        entityManager.getTransaction().commit();

        String notificationTitle = user.getNome() + " fez um comentário na lista " + lista.getNome();
        String notificationBody  = "Clique aqui para ir para o app";
        for (UsuarioGrupo usuarioGrupo : lista.getGrupoDeUsuarios().getUsuarios()) {
            try {
                this.notificator.notifyUser(usuarioGrupo.getUsuario(), notificationTitle, notificationBody);
            } catch (UnableToNotifyUserException e) {
                System.out.println("Ocorreu um erro ao notificar " + usuarioGrupo.getUsuario().getNome());
                System.out.println(e.getMessage());
            }
        }

        return lista.getComentarios();
    }

    /**
     * Altera as informações de uma lista
     * @param listId id da lista a ser atualizada
     * @param nomeLista nome a ser atribuído para a lista
     *
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public ListaDeCompras renameList(int listId, String nomeLista) throws ListNotFoundException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) throw new ListNotFoundException();

        lista.setNome(nomeLista);

        entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        return lista;
    }

    /**
     * Remove todos os produtos de uma lista e logo em seguida remove a lista
     * @param listId id da lista a ser removida
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public void deleteList(int listId) throws ListNotFoundException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) throw new ListNotFoundException();

        entityManager.getTransaction().begin();
        lista.getGrupoDeUsuarios().getListasDeCompras().remove(lista);
        entityManager.getTransaction().commit();

        lista.setProdutos(new HashSet<>());

        entityManager.getTransaction().begin();
        entityManager.remove(lista);
        entityManager.getTransaction().commit();
    }

    /**
     * Verifica se o campo informado está vazio.
     *
     * @param field_data dado de um campo especifico campo
     * @return
     */
    private boolean fieldIsEmpty(String field_data){

        return (field_data == null || field_data.equals("") || field_data.equals("\"\""));

    }

}
