package com.groupoffive.listapp.controllers;

import com.groupoffive.listapp.exceptions.CategoryNameAlreadyInUseException;
import com.groupoffive.listapp.exceptions.CategoryNotFoundException;
import com.groupoffive.listapp.models.Categoria;
import com.groupoffive.listapp.models.Produto;
import com.groupoffive.listapp.util.Levenshtein;
import com.groupoffive.listapp.util.MapSorter;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.*;

public class CategoriesController {

    private EntityManager entityManager;

    public CategoriesController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Devolve todas as categorias existentes
     * @return
     */
    public Set<Categoria> getCategories() {
        List<Categoria> lista = this.entityManager.createQuery("SELECT c FROM Categoria c").getResultList();
        return new HashSet<Categoria>(lista);
    }

    /**
     * Devolve uma lista de categorias recomendadas para o nome do produto inserido.
     * Verifica a similaridade do nome deste produto com o das categorias e seus produtos.
     * @param productName nome do produto a ser comparado.
     * @return
     */
    public Set<Categoria> getRecommendedCategories(String productName) {
        List<Categoria> lista       = this.entityManager.createQuery("SELECT c FROM Categoria c").getResultList();
        Set<Categoria> retorno      = new LinkedHashSet<>();
        Map<Categoria, Double> map  = new LinkedHashMap<>();

        /* Compara os nomes das categorias e seus produtos com o do produto digitado */
        for (Categoria categoria : lista) {
            Double distanciaNomeCategoria = Levenshtein.stringsDistance(productName, categoria.getNome());
            Double distanciaNomesProdutos = this.getDistanciaNomesProdutos(productName, categoria);

            map.put(categoria, Double.min(distanciaNomeCategoria, distanciaNomesProdutos));
        }

        /* Ordena o map de categorias pelas mais relevantes */
        Map mapProcessado = MapSorter.sortByValues(map);

        /* Preenchendo lista com os valores do map */
        mapProcessado.forEach((k,v) -> retorno.add((Categoria) k));

        return retorno;
    }

    private Double getDistanciaNomesProdutos(String productName, Categoria categoria) {
        Set<Produto> produtos = categoria.getProdutos();
        Double media          = produtos.size() > 0 ? 0d : 1d;

        /* Calcular distância  */
        for (Produto produto : produtos) {
            media += Levenshtein.stringsDistance(productName, produto.getNome()) / produtos.size();
        }

        return media;
    }

    /**
     * Traz os produtos pertencentes a uma categoria.
     * @param categoryId
     * @return
     * @throws CategoryNotFoundException
     */
    public Set<Produto> getProducts(int categoryId) throws CategoryNotFoundException {
        Categoria categoria = entityManager.find(Categoria.class, categoryId);

        if (null == categoria) throw new CategoryNotFoundException();

        return categoria.getProdutos();
    }

    /**
     * Adiciona uma categoria com o nome informado.
     * @param nome
     * @return
     * @throws CategoryNameAlreadyInUseException
     */
    public Categoria addCategory(String nome) throws CategoryNameAlreadyInUseException {
        if (this.categoryNameIsInUse(nome)) throw new CategoryNameAlreadyInUseException();

        Categoria categoria = new Categoria(nome);

        entityManager.getTransaction().begin();
        entityManager.persist(categoria);
        entityManager.getTransaction().commit();

        return categoria;
    }

    /**
     * Adiciona uma categoria com o nome informado.
     * Somente realizará commit, caso a variável canCommit seja true.
     * @param nome
     * @return
     * @throws CategoryNameAlreadyInUseException
     */
    Categoria addCategory(String nome, boolean canCommit) throws CategoryNameAlreadyInUseException {
        if (this.categoryNameIsInUse(nome)) throw new CategoryNameAlreadyInUseException();

        Categoria categoria = new Categoria(nome);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(categoria);
        if (canCommit) entityManager.getTransaction().commit();

        return categoria;
    }

    /**
     * Verifica se o nome informado para a categoria já está em uso.
     * @param nome
     * @return
     */
    private boolean categoryNameIsInUse(String nome) {
        try {
            Categoria categoria = entityManager.createQuery(
                    "SELECT c from Categoria c WHERE c.nome = :nome", Categoria.class
            ).setParameter("nome", nome).getSingleResult();

            return null != categoria;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Remove a categoria informada. Os produtos ficarão com idCategoria setado como null.
     * @param idCategoria
     */
    public void removeCategory(int idCategoria) throws CategoryNotFoundException {
        Categoria categoria = entityManager.find(Categoria.class, idCategoria);

        if (null == categoria) throw new CategoryNotFoundException();

        entityManager.getTransaction().begin();
        entityManager.remove(categoria);
        entityManager.getTransaction().commit();
    }

    /**
     * Atualiza os dados de uma categoria.
     * @param idCategoria
     * @param nome
     * @throws CategoryNotFoundException
     */
    public void updateCategory(int idCategoria, String nome) throws CategoryNotFoundException {
        Categoria categoria = entityManager.find(Categoria.class, idCategoria);

        if (null == categoria) throw new CategoryNotFoundException();

        entityManager.getTransaction().begin();
        categoria.setNome(nome);
        entityManager.getTransaction().commit();
    }

}
