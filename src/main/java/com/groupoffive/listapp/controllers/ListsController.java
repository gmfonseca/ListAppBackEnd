package com.groupoffive.listapp.controllers;

import com.groupoffive.listapp.exceptions.ListNotFoundException;
import com.groupoffive.listapp.models.ListaDeCompras;
import com.groupoffive.listapp.models.Produto;

import javax.persistence.EntityManager;
import java.util.Set;

public class ListsController {

    private EntityManager entityManager;

    public ListsController(EntityManager entityManager) {
        this.entityManager = entityManager;
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
     * Remove todos os produtos de uma lista e logo em seguida remove a lista
     * @param listId id da lista a ser removida
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public void deleteList(int listId) throws ListNotFoundException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) throw new ListNotFoundException();

        entityManager.getTransaction().begin();
        for (Produto produto : lista.getProdutos()) {
            entityManager.remove(produto);
        }
        entityManager.remove(lista);
        entityManager.getTransaction().commit();
    }

    /**
     * Altera as informações de uma lista
     * @param listId id da lista a ser atualizada
     * @param nomeLista nome a ser atribuído para a lista
     * @throws ListNotFoundException Exceção lançada caso lista com este id não seja encontrada
     */
    public void updateList(int listId, String nomeLista) throws ListNotFoundException {
        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, listId);

        if (null == lista) throw new ListNotFoundException();

        entityManager.getTransaction().begin();
        lista.setNome(nomeLista);
        entityManager.getTransaction().commit();
    }

}
