package com.groupoffive.listapp.routers;

import com.groupoffive.listapp.controllers.CategoriesController;
import com.groupoffive.listapp.exceptions.CategoryNameAlreadyInUseException;
import com.groupoffive.listapp.exceptions.CategoryNotFoundException;
import com.groupoffive.listapp.models.Categoria;
import com.groupoffive.listapp.models.Produto;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequestMapping("/categories")
public class CategoriesRouter {

    private CategoriesController categoriesController;

    public CategoriesRouter(CategoriesController categoriesController) {
        this.categoriesController = categoriesController;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public Set<Categoria> getCategories() {
        return categoriesController.getCategories();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Categoria getCategory(@PathVariable("id") int categoryId) throws CategoryNotFoundException{
        return categoriesController.getCategory(categoryId);
    }

    @RequestMapping(value = "/recommended/", method = RequestMethod.GET)
    @ResponseBody
    public Set<Categoria> getRecommendedCategories(String nomeProduto) {
        return categoriesController.getRecommendedCategories(nomeProduto);
    }

    @RequestMapping(value = "/{id}/products", method = RequestMethod.GET)
    @ResponseBody
    public Set<Produto> getProductsFromCategory(@PathVariable("id") int categoryId) throws CategoryNotFoundException {
        return categoriesController.getProducts(categoryId);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Categoria addCategory(String nome) throws CategoryNameAlreadyInUseException {
        return categoriesController.addCategory(nome);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Categoria updateCategory(@PathVariable("id") int idCategoria, String nome) throws CategoryNotFoundException {
        return categoriesController.updateCategory(idCategoria, nome);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeCategory(@PathVariable("id") int idCategoria) throws CategoryNotFoundException {
        categoriesController.removeCategory(idCategoria);
    }

}
