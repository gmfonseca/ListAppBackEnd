package com.groupoffive.listapp.util;

import com.groupoffive.listapp.exceptions.UnableToNotifyUserException;
import com.groupoffive.listapp.models.Dispositivo;
import com.groupoffive.listapp.models.Usuario;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

public class FirebaseNotificationService implements NotificationService {

    private EntityManager entityManager;
    private String key = "AIzaSyBSxxxxsXevRq0trDbA9mhnY_2jqMoeChA";

    public FirebaseNotificationService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void persistToken(Usuario usuario, String firebaseToken) {
        Dispositivo dispositivo = new Dispositivo(firebaseToken, usuario);

        this.entityManager.getTransaction().begin();
        for (Dispositivo d : this.getConnectedDevicesFromUser(usuario.getId())) {
            this.entityManager.remove(d);
        }

        this.entityManager.persist(dispositivo);
        this.entityManager.getTransaction().commit();
    }

    @Override
    public void notifyUser(Usuario usuario, String titulo, String mensagem) throws UnableToNotifyUserException {
        List<Dispositivo> dispositivos = this.getConnectedDevicesFromUser(usuario.getId());

        for (Dispositivo dispositivo : dispositivos) {
            String token = dispositivo.getFirebaseToken();
            this.sendNotificationToUser(token, titulo, mensagem);
        }
    }

    private List<Dispositivo> getConnectedDevicesFromUser(int userId) {
        return this.entityManager.createQuery(
            "SELECT d FROM Dispositivo d JOIN d.usuario u WHERE u.id = :idUsuario", Dispositivo.class
        ).setParameter("idUsuario", userId).getResultList();
    }

    private void sendNotificationToUser(String token, String messageTitle, String messageBody) throws UnableToNotifyUserException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
        post.setHeader("Content-type", "application/json");
        post.setHeader("Authorization", "key=" + this.key);

        try {
            JSONObject message = new JSONObject();
            message.put("to", token);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            notification.put("title", messageTitle);
            notification.put("body", messageBody);

            message.put("notification", notification);

            post.setEntity(new StringEntity(message.toString(), "UTF-8"));
            HttpResponse response = client.execute(post);
            System.out.println(response);
            System.out.println(message);
        } catch (JSONException | IOException e) {
            throw new UnableToNotifyUserException();
        }
    }

}
