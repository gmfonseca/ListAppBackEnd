package com.groupoffive.listapp.routers;

import com.groupoffive.listapp.controllers.ProductsController;
import com.groupoffive.listapp.exceptions.*;
import com.groupoffive.listapp.models.Produto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;

@RequestMapping("/products")
public class ProductsRouter {

    private ProductsController productsController;

    public ProductsRouter(ProductsController productsController) {
        this.productsController = productsController;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, params = { "nome", "preco", "idCategoria" })
    @ResponseBody
    public Produto addProduct(String nome, double preco, int idCategoria) throws ProductNameAlreadyInUseException, CategoryNotFoundException {
        return productsController.addProduct(nome, preco, idCategoria);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, params = { "nome", "preco", "nomeCategoria" })
    @ResponseBody
    public Produto addProduct(String nome, double preco, String nomeCategoria) throws ProductNameAlreadyInUseException, CategoryNameAlreadyInUseException {
        return productsController.addProduct(nome, preco, nomeCategoria);
    }

    @RequestMapping(value = "/recommended/", method = RequestMethod.GET, params = { "nomeProduto" })
    @ResponseBody
    public Set<Produto> getRecommendedProducts(String nomeProduto) {
        return productsController.getRecommendedProducts(nomeProduto);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<Produto> getAllProducts() {
        return productsController.getAllProducts();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, params = { "nome", "preco", "idCategoria" })
    @ResponseBody
    public Produto updateProduct(@PathVariable("id") int idProduto, String nome, double preco, int idCategoria)
            throws ProductNotFoundException, CategoryNotFoundException {
        return productsController.updateProduct(idProduto, nome, preco, idCategoria);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, params = { "nome", "preco", "nomeCategoria" })
    @ResponseBody
    public Produto updateProduct(@PathVariable("id") int idProduto, String nome, double preco, String nomeCategoria)
            throws ProductNotFoundException, CategoryNameAlreadyInUseException {
        return productsController.updateProduct(idProduto, nome, preco, nomeCategoria);
    }

    @RequestMapping(value = "/{id}/addToList/{idLista}", method = RequestMethod.PUT)
    @ResponseBody
    public void addProductToList(@PathVariable("id") int idProduto, @PathVariable("idLista") int idLista) throws ProductNotFoundException, ListNotFoundException {
        productsController.addProductToList(idProduto, idLista);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeProduct(@PathVariable("id") int idProduto) throws ProductNotFoundException {
        productsController.removeProduct(idProduto);
    }

    @RequestMapping(value = "/sendProductAcceptedNotification", method = RequestMethod.POST)
    @ResponseBody
    public void onProductAcceptedNotification(String nome, double preco, int idCategoria) {
        productsController.onProductAcceptedNotification(nome, preco, idCategoria);
    }

}
