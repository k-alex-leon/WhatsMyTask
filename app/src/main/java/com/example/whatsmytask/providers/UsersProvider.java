package com.example.whatsmytask.providers;

import com.example.whatsmytask.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference mCollection;

    public UsersProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }


    public Query getUserByEmail(String email){
        return mCollection.orderBy("email").startAt(email).endAt(email+'\uf8ff');
    }

    public Task<DocumentSnapshot> getUser(String id){
        return mCollection.document(id).get();
    }

    // Esta solicitando el modelo User de la carpeta models
    public Task<Void> create(User user){
       return mCollection.document(user.getId()).set(user);
    }

    public Task<Void> update (User user){
        // si se quiere actualizar mas valores simplemente se agrega un nuevo map.put
        Map<String,Object> map = new HashMap<>();
        map.put("userName", user.getUserName());
        map.put("timestamp", new Date().getTime());
        map.put("imageProfile", user.getImageProfile());
        return mCollection.document(user.getId()).update(map);
    }
}
