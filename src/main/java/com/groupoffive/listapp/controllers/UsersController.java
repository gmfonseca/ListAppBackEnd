package com.groupoffive.listapp.controllers;

import com.groupoffive.listapp.AppConfig;
import com.groupoffive.listapp.exceptions.EmailAlreadyInUseException;
import com.groupoffive.listapp.exceptions.IncorrectEmailOrPasswordException;
import com.groupoffive.listapp.exceptions.NotFilledRequiredFieldsException;
import com.groupoffive.listapp.exceptions.UserNotFoundException;
import com.groupoffive.listapp.models.GrupoDeUsuarios;
import com.groupoffive.listapp.models.Usuario;
import com.groupoffive.listapp.util.Crypt;
import com.groupoffive.listapp.util.NotificationService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.HashSet;
import java.util.Set;

public class UsersController {

    private EntityManager entityManager;
    private Crypt crypt;
    private NotificationService notificationService;

    public UsersController(Crypt crypt, NotificationService notificationService) {
        this.crypt               = crypt;
        this.notificationService = notificationService;
    }

    private Set<GrupoDeUsuarios> getGroupsFromUser(Usuario usuario) throws UserNotFoundException {
        entityManager = AppConfig.getEntityManager();

        if (null == usuario) throw new UserNotFoundException();

        Set<GrupoDeUsuarios> grupos = new HashSet<>();
        usuario.getGrupos().forEach(user_group -> grupos.add(user_group.getGrupo()));


        return grupos;
    }

    /**
     * Retorna os grupos aos quais um usuário está associado.
     * @param userId
     * @return
     * @throws UserNotFoundException caso o usuario solicitado nao esteja cadastrado ou nao esteja em algum grupo
     */
    public Set<GrupoDeUsuarios> getGroupsFromUser(int userId) throws UserNotFoundException {
        entityManager = AppConfig.getEntityManager();

        Usuario usuario = entityManager.find(Usuario.class, userId);


        return getGroupsFromUser(usuario);
    }

    /**
     * Valida o e-mail e senha informados e devolve os dados do usuário correspondente.
     *
     * @param email o email de login do usuario
     * @param senha a senha de login do usuario
     * @param FCMToken o token utilizado para notificações
     * @return
     * @throws IncorrectEmailOrPasswordException    caso o campo de senha e/ou email nao batam com algum registro da base de dados
     */
    public Usuario login(String email, String senha, String FCMToken) throws IncorrectEmailOrPasswordException {
        entityManager = AppConfig.getEntityManager();

        String cryptedPass = crypt.cryptString(senha);

        try {
            Usuario usuario = entityManager.createQuery(
                    "SELECT u from Usuario u WHERE u.email = :email AND u.senha = :senha", Usuario.class
            ).setParameter("email", email).setParameter("senha", cryptedPass).getSingleResult();

            if (FCMToken != null) this.notificationService.persistToken(usuario, FCMToken);


            return usuario;
        } catch (NoResultException e) {

            throw new IncorrectEmailOrPasswordException();
        }
    }

    /**
     * Adiciona um novo usuário com o nome, e-mail e senha informados.
     *
     * @param nome  o nome de login do usuario
     * @param email o email de login do usuario
     * @param senha a senha de login do usuario
     * @return
     * @throws NotFilledRequiredFieldsException     caso algum campo obrigatorio nao tenha sido preenchido
     * @throws EmailAlreadyInUseException           caso o email ja esteja cadastrado em outro usuario
     */
    public Usuario addUser(String nome, String email, String senha) throws NotFilledRequiredFieldsException, EmailAlreadyInUseException {
        entityManager = AppConfig.getEntityManager();

        if(fieldIsEmpty(nome) || fieldIsEmpty(email) || fieldIsEmpty(senha)) throw new NotFilledRequiredFieldsException();
        if(this.emailIsInUse(email)) throw new EmailAlreadyInUseException();

        String cryptedPass = crypt.cryptString(senha);

        Usuario usuario = new Usuario(nome, email, cryptedPass);

        if (!entityManager.getTransaction().isActive()) entityManager.getTransaction().begin();
        entityManager.persist(usuario);
        entityManager.getTransaction().commit();


        return usuario;
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

    /**
     * Verifica se o e-mail informado já está em uso por algum usuário.
     *
     * @param email o email de login do usuario
     * @return
     */
    private boolean emailIsInUse(String email) {
        try {
            Usuario usuario = entityManager.createQuery(
                    "SELECT u from Usuario u WHERE u.email = :email", Usuario.class
            ).setParameter("email", email).getSingleResult();

            return null != usuario;
        } catch (NoResultException e) {
            return false;
        }
    }

}
