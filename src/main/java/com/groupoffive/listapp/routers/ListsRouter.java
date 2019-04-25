package com.groupoffive.listapp.routers;

import com.groupoffive.listapp.controllers.ListsController;
import com.groupoffive.listapp.exceptions.*;
import com.groupoffive.listapp.models.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ListaDeCompras createList(String nome, int groupId) throws GroupNotFoundException {
        return listsController.createList(nome, groupId);
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
     * Obtem as categorias de uma lista
     * Método: GET
     * /lists/{id}/comments
     * @return
     * @throws ListNotFoundException
     */
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
    @ResponseBody
    public List<Object> getComments(@PathVariable("id") int listId) throws ListNotFoundException {
        return listsController.getComments(listId);
    }

    /**
     * Adiciona comentarios a uma lista
     * Método: PUT
     * /lists/{id}/comments
     * @param userId
     * @param comment
     * @return
     * @throws ListNotFoundException
     * @throws UserNotFoundException
     * @throws EmptyCommentException
     * @throws UserNotInGroupException
     */
    @RequestMapping(value = "/{id}/comments", method = RequestMethod.PUT)
    @ResponseBody
    public List<Object> addComment(@PathVariable("id") int listId, int userId, String comment)throws ListNotFoundException, UserNotFoundException, EmptyCommentException, UserNotInGroupException {
        return listsController.addComment(listId, userId, comment);
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
    public ListaDeCompras renameList(@PathVariable("id") int listId, String listName) throws ListNotFoundException {
        return listsController.renameList(listId, listName);
    }

    /**
     * Atualiza uma lista
     * Método: PUT
     * /lists/{id}/products/{productId}
     * @param listId
     * @param productId
     * @throws ListNotFoundException
     */
    @RequestMapping(value = "/{id}/products/{productId}", method = RequestMethod.PUT)
    @ResponseBody
    public ListaDeCompras addProduct(@PathVariable("id") int listId, @PathVariable("productId") int productId) throws ListNotFoundException, ProductNotFoundException, ProductAlreadyInListException {
        return listsController.addProduct(listId, productId);
    }

    /**
     * Adiciona comentarios a uma lista
     * Método: DELETE
     * /lists/{id}/comments/{commentId}
     * @param listId
     * @param userId
     * @param commentId
     *
     * @return
     *
     * @throws ListNotFoundException
     * @throws UserNotFoundException
     * @throws CommentNotFoundException
     * @throws UserNotInGroupException
     * @throws NotUserCommentException
     */
    @RequestMapping(value = "/{id}/comments/{commentId}", method = RequestMethod.DELETE)
    @ResponseBody
    public List<Object> deleteComment(@PathVariable("id") int listId, int userId, @PathVariable("commentId") int commentId)throws ListNotFoundException, UserNotFoundException, CommentNotFoundException, UserNotInGroupException, NotUserCommentException {
        return listsController.deleteComment(listId, userId, commentId);
    }

    /**
     * Remove uma lista
     * Método: DELETE
     * /lists/{id}/products/{productId}
     * @param listId
     * @param productId
     * @throws ListNotFoundException
     */
    @RequestMapping(value = "/{id}/products/{productId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeProduct(@PathVariable("id") int listId, @PathVariable("productId") int productId) throws ListNotFoundException, ProductNotFoundException, ProductDoesNotInListException {
        listsController.removeProduct(listId, productId);
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


}
