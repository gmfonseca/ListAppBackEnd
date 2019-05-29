package com.groupoffive.listapp.controllers;

import com.groupoffive.listapp.AppConfig;
import com.groupoffive.listapp.exceptions.*;
import com.groupoffive.listapp.models.*;
import com.groupoffive.listapp.util.NotificationService;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListsController {

    private EntityManager entityManager;
    private NotificationService notificator;

    public ListsController(NotificationService notificator) {
        this.notificator   = notificator;
    }

    public ListaDeCompras createList(String listName, int groupId) throws GroupNotFoundException {
        entityManager = AppConfig.getEntityManager();

        GrupoDeUsuarios grupo = entityManager.find(GrupoDeUsuarios.class, groupId);

        if (null == grupo) {
            entityManager.close();
            throw new GroupNotFoundException();
        }
        ListaDeCompras lista = new ListaDeCompras(listName, grupo);
        grupo.getListasDeCompras().add(lista);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        entityManager.close();
        return lista;
    }

    /**
     * Retorna os produtos pertencentes a uma lista.
     * @param listId id da lista a ser buscada
     * @return devolve os produtos da lista
     * @throws ListNotFoundException caso lista com este id não seja encontrada
     */
    public Set<Produto> getListProducts(int listId) throws ListNotFoundException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }

        entityManager.close();
        return lista.getProdutos();
    }

    /**
     * Retorna todos os comentarios de uma lista.
     * @param lista lista a ser buscada
     * @return comentarios da lista
     * @throws ListNotFoundException caso lista com este id não seja encontrada
     */
    private List<Object> getComments(ListaDeCompras lista) throws ListNotFoundException {
        entityManager = AppConfig.getEntityManager();

        if(null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }

        List<Object> comentarios = Collections.singletonList(lista.getComentarios());

        entityManager.close();
        return comentarios;
    }
    public List<Object> getComments(int listId) throws ListNotFoundException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        entityManager.close();
        return getComments(lista);
    }

    /**
     * Retorna as categories pertencentes a uma lista.
     * @param listId id da lista a ser buscada
     * @return devolve as categorias da lista
     * @throws ListNotFoundException caso lista com este id não seja encontrada
     */
    public Set<Categoria> getListCategories(int listId) throws ListNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Set<Produto> produtos;
        Set<Categoria> categorias = new HashSet<>();
        ListaDeCompras lista  = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }

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

        entityManager.close();
        return categorias;
    }

    public ListaDeCompras addProduct(int listId, int productId) throws ListNotFoundException, ProductNotFoundException, ProductAlreadyInListException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);
        Produto produto = entityManager.find(Produto.class, productId);

        if (null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }
        if (null == produto) {
            entityManager.close();
            throw new ProductNotFoundException();
        }
        if (lista.getProdutos().contains(produto)) {
            entityManager.close();
            throw new ProductAlreadyInListException();
        }

        lista.getProdutos().add(produto);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        entityManager.close();
        return lista;
    }

    public ListaDeCompras removeProduct(int listId, int productId) throws ListNotFoundException, ProductNotFoundException, ProductDoesNotInListException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);
        Produto produto = entityManager.find(Produto.class, productId);

        if (null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }
        if(null == produto) {
            entityManager.close();
            throw new ProductNotFoundException();
        }
        if(!lista.getProdutos().contains(produto)) {
            entityManager.close();
            throw new ProductDoesNotInListException();
        }

        lista.getProdutos().remove(produto);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        entityManager.close();
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
    public List<Object> addComment(int listId, int userId, String comment)throws ListNotFoundException, UserNotFoundException, EmptyCommentException, UserNotInGroupException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);
        Usuario user = entityManager.find(Usuario.class, userId);

        if (null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }
        if (null == user) {
            entityManager.close();
            throw new UserNotFoundException();
        }
        if (fieldIsEmpty(comment)) {
            entityManager.close();
            throw new EmptyCommentException();
        }
        if (!lista.getGrupoDeUsuarios().containsUser(user)) {
            entityManager.close();
            throw new UserNotInGroupException();
        }

        Comentario comentario = new Comentario(comment);

        ComentarioLista cl = new ComentarioLista(user, lista, comentario);

        user.getComentarios().add(cl);
        lista.getComentarios().add(cl);
        comentario.getListas().add(cl);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(comentario);
        entityManager.persist(cl);
        entityManager.getTransaction().commit();

        String notificationTitle = "Um novo comentário foi feito na lista " + lista.getNome();
        String notificationBody  = user.getNome() + ": \"" + comment + "\"";
        for (UsuarioGrupo usuarioGrupo : lista.getGrupoDeUsuarios().getUsuarios()) {
            try {
                this.notificator.notifyUser(usuarioGrupo.getUsuario(), notificationTitle, notificationBody);
            } catch (UnableToNotifyUserException e) {
                entityManager.close();
                System.out.println("Ocorreu um erro ao notificar " + usuarioGrupo.getUsuario().getNome());
                System.out.println(e.getMessage());
            }
        }

        entityManager.close();
        return getComments(lista);
    }

    /**
     * Altera as informações de uma lista
     * @param listId id da lista a ser atualizada
     * @param nomeLista nome a ser atribuído para a lista
     *
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public ListaDeCompras renameList(int listId, String nomeLista) throws ListNotFoundException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }

        lista.setNome(nomeLista);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();

        entityManager.close();
        return lista;
    }

    /**
     * Usuario apaga um de seus comentarios de uma lista
     * @param listId id da lista onde esta o comentario
     * @param userId id do usuario autor do comentario
     * @param commentId id do comentario a ser removido
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public List<Object> deleteComment(int listId, int userId, int commentId)
            throws ListNotFoundException, UserNotFoundException, CommentNotFoundException, UserNotInGroupException, NotUserCommentException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras list = entityManager.find(ListaDeCompras.class, listId);
        Usuario user = entityManager.find(Usuario.class, userId);
        Comentario comment = entityManager.find(Comentario.class, commentId);

        if(list == null) {
            entityManager.close();
            throw new ListNotFoundException();
        }
        if(user == null) {
            entityManager.close();
            throw new UserNotFoundException();
        }
        if(comment == null) {
            entityManager.close();
            throw new CommentNotFoundException();
        }
        if(!list.getGrupoDeUsuarios().containsUser(user)) throw new UserNotInGroupException();

        ComentarioListaPK clPK = new ComentarioListaPK(user, list, comment);
        ComentarioLista cl = entityManager.find(ComentarioLista.class, clPK);

        if(cl == null) throw new NotUserCommentException();

        list.getComentarios().remove(cl);
        user.getComentarios().remove(cl);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.remove(cl);
        entityManager.remove(comment);
        entityManager.getTransaction().commit();

        entityManager.close();
        return getComments(list);
    }

    /**
     * Remove todos os produtos de uma lista e logo em seguida remove a lista
     * @param listId id da lista a ser removida
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public void deleteList(int listId) throws ListNotFoundException {
        entityManager = AppConfig.getEntityManager();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) {
            entityManager.close();
            throw new ListNotFoundException();
        }

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        lista.getGrupoDeUsuarios().getListasDeCompras().remove(lista);
        entityManager.getTransaction().commit();

        lista.setProdutos(new HashSet<>());

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.remove(lista);
        entityManager.getTransaction().commit();

        entityManager.close();
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
