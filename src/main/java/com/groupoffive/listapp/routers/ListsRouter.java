package com.groupoffive.listapp.routers;

import com.groupoffive.listapp.controllers.ListsController;
import com.groupoffive.listapp.exceptions.GroupNotFoundException;
import com.groupoffive.listapp.exceptions.ListNotFoundException;
import com.groupoffive.listapp.models.Categoria;
import com.groupoffive.listapp.models.Produto;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/lists")
public class ListsRouter {

    private ListsController listsController;

    public ListsRouter(ListsController listsController) {
        this.listsController = listsController;
    }

    /**
     * Cria uma nova lista relacionando-a a um grupo
     * Método: POST
     * /lists/
     * @param nome
     * @param groupId
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public void createList(String nome, int groupId) throws GroupNotFoundException {
        listsController.createList(nome, groupId);
    }

    /**
     * Obtem os produtos de uma lista
     * Método: GET
     * /lists/{id}/products
     * @param listId
     * @return
     * @throws ListNotFoundException
     */
    @RequestMapping(value = "/{id}/products", method = RequestMethod.GET)
    @ResponseBody
    public Set<Produto> getListProducts(@PathVariable("id") int listId) throws ListNotFoundException {
        return listsController.getListProducts(listId);
    }

    /**
     * Obtem as categorias de uma lista
     * Método: GET
     * /lists/{id}/categories
     * @param listId
     * @return
     * @throws ListNotFoundException
     */
    @RequestMapping(value = "/{id}/categories", method = RequestMethod.GET)
    @ResponseBody
    public Set<Categoria> getListCategories(@PathVariable("id") int listId) throws ListNotFoundException {
        return listsController.getListCategories(listId);
    }

    /**
     * Remove uma lista
     * Método: DELETE
     * /lists/{id}
     * @param listId
     * @throws ListNotFoundException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteList(@PathVariable("id") int listId) throws ListNotFoundException {
        listsController.deleteList(listId);
    }

    /**
     * Atualiza uma lista
     * Método: PUT
     * /lists/{id}
     * @param listId
     * @param listName
     * @throws ListNotFoundException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateList(@PathVariable("id") int listId, String listName) throws ListNotFoundException {
        listsController.updateList(listId, listName);
    }

}
