package com.groupoffive.listapp.controllers;

import com.groupoffive.listapp.AppConfig;
import com.groupoffive.listapp.exceptions.*;
import com.groupoffive.listapp.models.Categoria;
import com.groupoffive.listapp.models.ListaDeCompras;
import com.groupoffive.listapp.models.Produto;
import com.groupoffive.listapp.models.Usuario;
import com.groupoffive.listapp.util.*;
import com.groupoffive.listapp.util.Queue;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

//import com.groupoffive.listapp.models.ProdutoLista;

public class ProductsController {

    private EntityManager entityManager;
    private Queue queue;

    public ProductsController(Queue queue) {
        this.queue         = queue;
    }

    public List<Produto> getAllProducts() {
        entityManager = AppConfig.getEntityManager();

        List<Produto> products = entityManager.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();

        return products;
    }

    public Set<Produto> getRecommendedProducts(String nomeProduto) {
        entityManager = AppConfig.getEntityManager();

        List<Produto> lista         = entityManager.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        Set<Produto> retorno        = new LinkedHashSet<>();
        Map<Produto, Double> map    = new LinkedHashMap<>();

        /* Compara os nomes dos produtos com o do produto digitado */
        for (Produto produto : lista) {
            Double distancia = Levenshtein.stringsDistance(nomeProduto, produto.getNome());
            if (distancia < 0.4d) map.put(produto, distancia);
        }

        /* Ordena o map de produtos pelos mais relevantes */
        Map mapProcessado = MapSorter.sortByValues(map);

        /* Preenchendo lista com os valores do map */
        mapProcessado.forEach((k,v) -> retorno.add((Produto) k));


        return retorno;
    }

    /**
     * Adiciona um novo produto a uma categoria existente.
     * @param nome
     * @param preco
     * @param idCategoria
     * @return
     */
    public Produto addProduct(String nome, double preco, int idCategoria, int idUsuario) throws ProductNameAlreadyInUseException, CategoryNotFoundException {
        entityManager = AppConfig.getEntityManager();

        if (this.productNameIsInUse(nome)) throw new ProductNameAlreadyInUseException();

        Categoria categoria = this.entityManager.find(Categoria.class, idCategoria);
        if (null == categoria) throw new CategoryNotFoundException();

        Produto produto = new Produto(nome, preco, categoria);

        this.queue.sendMessageToQueue(
                "{ \"nomeProduto\": \"" + nome + "\", \"preco\": " + preco + ", \"idCategoria\": " + idCategoria + ", \"idUsuario\": " + idUsuario + "}",
                AmazonQueue.QUEUE_PRODUCT_ANALYSE
        );


        return produto;
    }

    public void onProductAcceptedNotification(String nome, double preco, int idCategoria, int idUsuario) {
        entityManager = AppConfig.getEntityManager();

        Categoria categoria = this.entityManager.find(Categoria.class, idCategoria);
        Produto produto = new Produto(nome, preco, categoria);


        try {
            Usuario usuario = this.entityManager.find(Usuario.class, idUsuario);
            new FirebaseNotificationService().notifyUser(usuario, "Notícia sobre seu produto", "Seu produto " + nome + " foi aprovado e adicionado no banco de dados.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        categoria.getProdutos().add(produto);
        entityManager.persist(produto);
        entityManager.getTransaction().commit();


    }

    /**
     * Adiciona um novo produto a uma nova categoria.
     * @param nome
     * @param preco
     * @param nomeCategoria
     * @return
     * @throws ProductNameAlreadyInUseException
     * @throws CategoryNameAlreadyInUseException
     */
    public Produto addProduct(String nome, double preco, String nomeCategoria, int idUsuario) throws ProductNameAlreadyInUseException, CategoryNameAlreadyInUseException {
        entityManager = AppConfig.getEntityManager();

        try {
            Categoria categoria = AppConfig.getContext().getBean("categoriesController", CategoriesController.class).addCategory(nomeCategoria, true);
            this.queue.sendMessageToQueue(
                    "{ \"nomeProduto\": \"" + nome + "\", \"preco\": " + preco + ", \"idCategoria\": " + categoria.getId() + ", \"idUsuario\": " + idUsuario + "}",
                    AmazonQueue.QUEUE_PRODUCT_ANALYSE
            );
            System.out.println("{ \"nomeProduto\": \"" + nome + "\", \"preco\": " + preco + ", \"idCategoria\": " + categoria.getId());
            return this.addProduct(nome, preco, categoria.getId(), idUsuario);
        } catch (CategoryNotFoundException e) {
            // Tenho muita fé de que isso não vai acontecer

            return null;
        }
    }

    /**
     * Verifica se o nome informado para o produto já está em uso.
     * @param nome
     * @return
     */
    private boolean productNameIsInUse(String nome) {
        try {
            Produto produto = entityManager.createQuery(
                    "SELECT p from Produto p WHERE p.nome = :nome", Produto.class
            ).setParameter("nome", nome).getSingleResult();

            return null != produto;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Altera os dados de um produto. Atribui ele a uma categoria já existente.
     * @param idProduto
     * @param nome
     * @param preco
     * @param idCategoria
     * @throws ProductNotFoundException
     * @throws CategoryNotFoundException
     */
    public Produto updateProduct(int idProduto, String nome, double preco, int idCategoria)
            throws ProductNotFoundException, CategoryNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Produto produto     = entityManager.find(Produto.class, idProduto);
        Categoria categoria = entityManager.find(Categoria.class, idCategoria);

        if (null == produto) throw new ProductNotFoundException();
        if (null == categoria) throw new CategoryNotFoundException();

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setCategoria(categoria);
        entityManager.getTransaction().commit();


        return produto;
    }

    /**
     * Altera os dados de um produto. Atribui ele a uma nova categoria.
     * @param nome
     * @param preco
     * @param nomeCategoria
     * @return
     * @throws ProductNameAlreadyInUseException
     * @throws CategoryNameAlreadyInUseException
     */
    public Produto updateProduct(int idProduto, String nome, double preco, String nomeCategoria) throws ProductNotFoundException, CategoryNameAlreadyInUseException {
        entityManager = AppConfig.getEntityManager();

        try {
            Categoria categoria = AppConfig.getContext().getBean("categoriesController", CategoriesController.class).addCategory(nomeCategoria, false);
            return this.updateProduct(idProduto, nome, preco, categoria.getId());
        } catch (CategoryNotFoundException e) {
            // Tenho muita fé de que isso não vai acontecer

            return null;
        }
    }

    public void addProductToList(int idProduto, int idLista) throws ProductNotFoundException, ListNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Produto produto      = entityManager.find(Produto.class, idProduto);
        if (null == produto) throw new ProductNotFoundException();

        ListaDeCompras lista = entityManager.find(ListaDeCompras.class, idLista);
        if (null == lista) throw new ListNotFoundException();

//        ProdutoLista pl      = new ProdutoLista(lista, produto);
        lista.getProdutos().add(produto);
        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(lista);
        entityManager.getTransaction().commit();


    }


    /**
     * Remove o produto informado.
     * @param idProduto
     * @throws ProductNotFoundException
     */
    public void removeProduct(int idProduto) throws ProductNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Produto produto = entityManager.find(Produto.class, idProduto);

        if (null == produto) throw new ProductNotFoundException();

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.remove(produto);
        entityManager.getTransaction().commit();


    }
}
