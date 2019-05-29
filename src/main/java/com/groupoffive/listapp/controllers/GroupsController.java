package com.groupoffive.listapp.controllers;

import com.groupoffive.listapp.AppConfig;
import com.groupoffive.listapp.exceptions.*;
import com.groupoffive.listapp.models.GrupoDeUsuarios;
import com.groupoffive.listapp.models.Usuario;
import com.groupoffive.listapp.models.UsuarioGrupo;
import com.groupoffive.listapp.models.UsuarioGrupoPK;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupsController {

    private EntityManager entityManager;

    public GroupsController() {}

    /**
     * Retorna os grupos aos quais um usuario esta associado.
     *
     * @param usuario usuario que se deseja recuperar os grupos
     * @return lista dos grupos que o usuario esta presente
     *
     * @throws UserNotFoundException caso o usuario solicitado nao esteja cadastrado ou nao esteja em algum grupo
     */
    private Set<GrupoDeUsuarios> getGroupsFromUser(Usuario usuario) throws UserNotFoundException {
        entityManager = AppConfig.getEntityManager();

        if (null == usuario) {
            entityManager.close();
            throw new UserNotFoundException();
        }

        Set<GrupoDeUsuarios> grupos = new HashSet<>();
        usuario.getGrupos().forEach(user_group -> grupos.add(user_group.getGrupo()));

        entityManager.close();
        return grupos;
    }

    public Set<GrupoDeUsuarios> getGroupsFromUser(int userId) throws UserNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Usuario usuario = entityManager.find(Usuario.class, userId);

        entityManager.close();
        return getGroupsFromUser(usuario);
    }

    /**
     * Retorna os usuarios associados a um grupo.
     *
     * @param group grupo que se deseja recuperar os usuarios
     * @return lista dos usuarios presentes no grupo
     *
     * @throws GroupNotFoundException caso o grupo solicitado nao esteja cadastrado
     */
    private Set<Usuario> getUsersFromGroup(GrupoDeUsuarios group) throws GroupNotFoundException{
        entityManager = AppConfig.getEntityManager();

        if(null == group) {
            entityManager.close();
            throw new GroupNotFoundException();
        }

        Set<Usuario> users = new HashSet<>();

        group.getUsuarios().forEach(user_group -> {
            users.add(user_group.getUsuario());
        });

        entityManager.close();
        return users;
    }
    public Set<Usuario> getUsersFromGroup(int groupId) throws GroupNotFoundException {
        entityManager = AppConfig.getEntityManager();

        GrupoDeUsuarios grupo = entityManager.find(GrupoDeUsuarios.class, groupId);

        entityManager.close();
        return getUsersFromGroup(grupo);
    }

    /**
     * Metodo para recuperar um grupo especifico pelo seu id.
     *
     * @param groupId id do grupo que se deseja recuperar
     * @return grupo que se deseja recuperar
     *
     * @throws GroupNotFoundException caso o grupo solicitado nao esteja cadastrado
     */
    public GrupoDeUsuarios getGroup(int groupId) throws GroupNotFoundException{
        entityManager = AppConfig.getEntityManager();

        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, groupId);

        if(group == null) {
            entityManager.close();
            throw new GroupNotFoundException();
        }

        entityManager.close();
        return group;
    }

    /**
     * Metodo para criar um grupo com os dados passados por parametro
     *
     * @param nome nomeação do grupo a ser criado
     * @param criador usuario que criou o grupo
     *
     * @return grupo criado com os dados passados
     *
     * @throws UserNotFoundException caso o usuario solicitado nao esteja cadastrado
     */
    private GrupoDeUsuarios createGroup(String nome, Usuario criador) throws UserNotFoundException{
        entityManager = AppConfig.getEntityManager();

        if(criador == null) {
            entityManager.close();
            throw new UserNotFoundException();
        }

        GrupoDeUsuarios group = new GrupoDeUsuarios(nome, criador);
        UsuarioGrupo ug = new UsuarioGrupo(criador, group, true);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(group);
        entityManager.persist(ug);
        entityManager.getTransaction().commit();

        entityManager.close();
        return group;
    }
    public GrupoDeUsuarios createGroup(String nome, int creator_id) throws UserNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Usuario criador = entityManager.find(Usuario.class, creator_id);

        entityManager.close();
        return createGroup(nome, criador);
    }
    public GrupoDeUsuarios createGroup(String nome, String creator_name) throws UserNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Usuario criador = getUserByName(creator_name);

        entityManager.close();
        return createGroup(nome, criador);
    }

    /**
     * Metodo para adicionar um usuario a um grupo
     *
     * @param user usuario a ser inserido em um grupo
     * @param group grupo que o usuario será inserido
     *
     * @return grupo com o usuario inserido
     *
     * @throws UserNotFoundException  caso o usuario solicitado nao esteja cadastrado
     * @throws GroupNotFoundException caso o grupo solicitado nao esteja cadastrado
     * @throws UserAlreadyInGroupException caso o usuario ja esteja inserido no respectivo grupo
     */
    private GrupoDeUsuarios addUserToGroup(Usuario user, GrupoDeUsuarios group) throws GroupNotFoundException, UserNotFoundException, UserAlreadyInGroupException {
        entityManager = AppConfig.getEntityManager();

        if(group == null) {
            entityManager.close();
            throw new GroupNotFoundException();
        }
        if(user == null) {
            entityManager.close();
            throw new UserNotFoundException();
        }

        if(getUsersFromGroup(group).contains(user)) {
            entityManager.close();
            throw new UserAlreadyInGroupException();
        }

        boolean admin=false;

        if(group.getCriador().equals(user)){
            admin=true;
        }

        UsuarioGrupo ug = new UsuarioGrupo(user, group, admin);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(group);
        entityManager.persist(ug);
        entityManager.getTransaction().commit();

        entityManager.close();
        return group;
    }
    public GrupoDeUsuarios addUserToGroup(int user_id, int group_id) throws GroupNotFoundException, UserNotFoundException, UserAlreadyInGroupException {
        entityManager = AppConfig.getEntityManager();

        Usuario user = entityManager.find(Usuario.class, user_id);
        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, group_id);

        entityManager.close();
        return addUserToGroup(user, group);
    }

    public GrupoDeUsuarios addUserToGroup(String email, int group_id) throws GroupNotFoundException, UserNotFoundException, UserAlreadyInGroupException {
        entityManager = AppConfig.getEntityManager();

        Usuario user = getUserByEmail(email);
        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, group_id);

        entityManager.close();
        return addUserToGroup(user, group);
    }

    /**
     * Metodo para remover um usuario de um grupo especifico
     *
     * @param user usuario a ser removido de um grupo
     * @param group grupo que o usuario sera removido
     *
     * @return grupo sem o usuario que foi removido
     *
     * @throws UserNotFoundException  caso o usuario solicitado nao esteja cadastrado
     * @throws GroupNotFoundException caso o grupo solicitado nao esteja cadastrado
     * @throws UserNotInGroupException caso o usuario nao esteja inserido no respectivo grupo
     */
    private GrupoDeUsuarios removeUserFromGroup(Usuario user, GrupoDeUsuarios group) throws GroupNotFoundException, UserNotFoundException, UserNotInGroupException {
        entityManager = AppConfig.getEntityManager();

        if(group == null) {
            entityManager.close();
            throw new GroupNotFoundException();
        }
        if(user == null) {
            entityManager.close();
            throw new UserNotFoundException();
        }

        Set<Usuario> users = getUsersFromGroup(group);

        if(!users.contains(user)) {
            entityManager.close();
            throw new UserNotInGroupException();
        }

        boolean hasAdmin=false;

        try {

            UsuarioGrupo ug = entityManager.find(UsuarioGrupo.class, new UsuarioGrupoPK(user, group));

            group.getUsuarios().remove(ug);
            user.getGrupos().remove(ug);

            if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
            entityManager.remove(ug);
            entityManager.getTransaction().commit();

            if (group.getUsuarios().size() > 0) {

                //Definir um usuario como admin, caso nao tenha algum outro
                if (ug.isAdmin()) {
                    for (UsuarioGrupo usuario : group.getUsuarios()) {
                        if (usuario.isAdmin()) {
                            hasAdmin = true;
                            break;
                        }
                    }

                    if (!hasAdmin) {
                        UsuarioGrupo bkp = (UsuarioGrupo) group.getUsuarios().toArray()[0];
                        bkp.setAdmin(true);
                    }
                }

                if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
                entityManager.persist(group);
                entityManager.getTransaction().commit();
                entityManager.close();
            } else {
                //Deletar o grupo, caso nao tenha mais usuarios
                deleteGroup(group, group.getCriador());
            }

        }catch (Exception e){
            entityManager.close();
        }


        return group;
    }

    public GrupoDeUsuarios removeUserFromGroup(int user_id, int group_id) throws GroupNotFoundException, UserNotFoundException, UserNotInGroupException {
        entityManager = AppConfig.getEntityManager();

        Usuario user = entityManager.find(Usuario.class, user_id);
        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, group_id);

        entityManager.close();
        return removeUserFromGroup(user, group);
    }

    public GrupoDeUsuarios removeUserFromGroup(String user_name, int group_id) throws GroupNotFoundException, UserNotFoundException, UserNotInGroupException {
        entityManager = AppConfig.getEntityManager();

        Usuario user = getUserByName(user_name);
        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, group_id);

        entityManager.close();
        return removeUserFromGroup(user, group);
    }

    /**
     * Metodo para alterar o status de admin de um usuario especifico em um grupo especifico
     *
     * @param user usuario a ter status de admin alterado no grupo
     * @param group grupo que o usuario tera status de admin alterado
     *
     * @return grupo com status de admin alterado do usuario requisitado
     *
     * @throws GroupNotFoundException caso o grupo solicitado nao esteja cadastrado
     * @throws UserNotFoundException  caso o usuario solicitado nao esteja cadastrado
     * @throws UserNotInGroupException caso o usuario nao esteja inserido no respectivo grupo
     * @throws UserGroupCreatorException caso o usuario seja o criador do grupo, nao podendo alterar status de admin
     * @throws UserAlreadyGroupAdminException caso o usuario ja seja admin no respectivo grupo
     * @throws UserWasNotGroupAdminException caso o usuario nao seja admin no respectivo grupo
     */
    private GrupoDeUsuarios toggleUserGroupAdmin(Usuario user, GrupoDeUsuarios group, boolean admin)
            throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, UserGroupCreatorException, UserAlreadyGroupAdminException, UserWasNotGroupAdminException {

        entityManager = AppConfig.getEntityManager();

        if(group == null) {
            entityManager.close();
            throw new GroupNotFoundException();
        }
        if(user == null) {
            entityManager.close();
            throw new UserNotFoundException();
        }

        if(!group.containsUser(user)) {
            entityManager.close();
            throw new UserNotInGroupException();
        }
        if(group.getCriador().equals(user)) {
            entityManager.close();
            throw new UserGroupCreatorException();
        }

        UsuarioGrupo ug = entityManager.find(UsuarioGrupo.class, new UsuarioGrupoPK(user, group));

        if(ug.isAdmin() && admin) {
            entityManager.close();
            throw new UserAlreadyGroupAdminException();
        }
        if(!ug.isAdmin() && !admin) {
            entityManager.close();
            throw new UserWasNotGroupAdminException();
        }

        group.getUsuarios().remove(ug);
        user.getGrupos().remove(ug);

        ug.setAdmin(admin);

        group.getUsuarios().add(ug);
        user.getGrupos().add(ug);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(group);
        entityManager.persist(ug);
        entityManager.getTransaction().commit();

        entityManager.close();
        return group;
    }
    public GrupoDeUsuarios toggleUserGroupAdmin(int userId, int groupId, boolean admin)
            throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, UserGroupCreatorException, UserAlreadyGroupAdminException, UserWasNotGroupAdminException {

        entityManager = AppConfig.getEntityManager();

        Usuario user = entityManager.find(Usuario.class, userId);
        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, groupId);

        entityManager.close();
        return toggleUserGroupAdmin(user, group, admin);
    }
    public GrupoDeUsuarios toggleUserGroupAdmin(String userName, int groupId, boolean admin)
            throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, UserGroupCreatorException, UserAlreadyGroupAdminException, UserWasNotGroupAdminException {

        entityManager = AppConfig.getEntityManager();

        Usuario user = getUserByName(userName);
        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, groupId);

        entityManager.close();
        return toggleUserGroupAdmin(user, group, admin);
    }

    /**
     * Metodo para deletar um grupo especifico
     *
     * @param group grupo que sera deletado
     * @return grupo que foi deletado
     *
     * @throws GroupNotFoundException caso o grupo solicitado nao esteja cadastrado
     */
    private GrupoDeUsuarios deleteGroup(GrupoDeUsuarios group, Usuario eraserUser) throws GroupNotFoundException, UserNotFoundException, NotGroupOwnerException {

        entityManager = AppConfig.getEntityManager();

        if (group == null) {
            entityManager.close();
            throw new GroupNotFoundException();
        }
        if (eraserUser == null) {
            entityManager.close();
            throw new UserNotFoundException();
        }

        if(!group.getCriador().equals(eraserUser)) throw new NotGroupOwnerException();

        try {

            List<UsuarioGrupo> associacoes = new ArrayList<>();
            List<Usuario> usuarios = new ArrayList<>();

            group.getUsuarios().forEach(user_group -> {
                if(user_group.getGrupo().equals(group)){
                    associacoes.add(user_group);
                }
            });

            associacoes.forEach(user_group -> {
                group.getUsuarios().remove(user_group);
                user_group.getUsuario().getGrupos().remove(user_group);
                usuarios.add(user_group.getUsuario());
            });

            group.setUsuarios(new HashSet<>());
            if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
            usuarios.forEach(usuario -> {
                UsuarioGrupo ug = entityManager.find(UsuarioGrupo.class, new UsuarioGrupoPK(usuario, group));

                entityManager.remove(ug);
            });
            entityManager.getTransaction().commit();

            if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
            entityManager.remove(group);
            entityManager.getTransaction().commit();
            entityManager.close();
        }catch (Exception e){
            entityManager.close();
        }


        return group;
    }
    public GrupoDeUsuarios deleteGroup(int groupId, int eraserUserId) throws GroupNotFoundException, UserNotFoundException, NotGroupOwnerException {
        entityManager = AppConfig.getEntityManager();

        GrupoDeUsuarios group = entityManager.find(GrupoDeUsuarios.class, groupId);
        Usuario eraserUser = entityManager.find(Usuario.class, eraserUserId);

        entityManager.close();
        return deleteGroup(group, eraserUser);
    }

    /**
     * Metodo para buscar um usuario especifico pelo nome
     *
     * @param nome nome do usuario que deseja buscar
     * @return usuario encontrado
     */
    private Usuario getUserByName(String nome){
        try{


            return entityManager.createQuery("SELECT u from Usuario u WHERE u.nome = :nome", Usuario.class).setParameter("nome", nome).getSingleResult();

        }catch(NoResultException e){

            return null;
        }
    }

    /**
     * Metodo para buscar um usuario especifico pelo email
     *
     * @param email email do usuario que deseja buscar
     * @return usuario encontrado
     */
    private Usuario getUserByEmail(String email){
        try{


            return entityManager.createQuery("SELECT u from Usuario u WHERE u.email = :email", Usuario.class).setParameter("email", email).getSingleResult();

        }catch(NoResultException e){

            return null;
        }
    }
}
