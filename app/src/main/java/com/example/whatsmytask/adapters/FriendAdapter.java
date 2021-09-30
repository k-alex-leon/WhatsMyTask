package com.example.whatsmytask.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.FCMBody;
import com.example.whatsmytask.models.FCMResponse;
import com.example.whatsmytask.models.Friend;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.FriendsProvider;
import com.example.whatsmytask.providers.NotificationProvider;
import com.example.whatsmytask.providers.TokenProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.example.whatsmytask.utils.ItemChecked;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendAdapter extends FirestoreRecyclerAdapter<User, FriendAdapter.ViewHolder>{

    Context context;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    FriendsProvider mFriendProvider;
    UsersProvider mUserProvider;
    String idUserFriend, idUserProfile, bodyNoti;



    public FriendAdapter(FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }




    // ESTABLECE EL CONTENIDO QUE SE QUIERE MOSTRAR
    @Override
    protected void onBindViewHolder(@NotNull FriendAdapter.ViewHolder holder, int position,@NotNull User user) {

        idUserProfile = mAuthProvider.getUid();

        idUserFriend = user.getId();
            holder.textViewNameFriend.setText(user.getUserName());
            holder.textViewEmailFriend.setText(user.getEmail());
            if(user.getImageProfile() != null){
                if(!user.getImageProfile().isEmpty()){
                    Picasso.with(context).load(user.getImageProfile()).into(holder.mImageProfileViewFriend);
                }
            }

            if (idUserProfile.contentEquals(idUserFriend)){
                holder.mImageViewAddFriend.setVisibility(View.GONE);
            }else{
                holder.mImageViewAddFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showConfirmAddFriend();
                    }
                });

            }


    }


    //se encarga de mostrar una alerta antes de agregar contacto
    private void showConfirmAddFriend() {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_input_add)
                .setTitle("Add firend")
                .setMessage("Do you want add this person?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mUserProvider.getUser(idUserProfile).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.contains("userName")){
                                    bodyNoti = documentSnapshot.getString("userName");
                                    sendNotification(bodyNoti);
                                    createFriend(idUserFriend);
                                }

                            }
                        });


                    }
                })
                .setNegativeButton("No", null)
                .show();

    }


    private void createFriend(String idUserFriend){
        Friend friend = new Friend();
        friend.setIdUser1(idUserProfile);
        friend.setIdUser2(idUserFriend);

        mFriendProvider.createFriend(friend);
    }


    // enviar notificacion de nuevo contacto al otro usuario
    private void sendNotification(String bodyNoti) {
        if(idUserFriend == null){
            return;
        }
        mTokenProvider.getToken(idUserFriend).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "New friend!");
                        data.put("body", bodyNoti);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body() != null){
                                    if(response.body().getSuccess() == 1){
                                        Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(context, "Error with message sent", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context, "Error with message sent", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }else{
                    Toast.makeText(context, "Token doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_add_friend,parent,false);
        return new ViewHolder(view);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNameFriend;
        TextView textViewEmailFriend;
        TextView textViewNumberFriend;
        ImageView mImageProfileViewFriend;
        ImageView mImageViewAddFriend;


        public ViewHolder(View view) {
            super(view);

            mAuthProvider = new AuthProvider();
            mFriendProvider = new FriendsProvider();
            mUserProvider = new UsersProvider();
            mTokenProvider = new TokenProvider();
            mNotificationProvider = new NotificationProvider();
        textViewNameFriend = view.findViewById(R.id.textViewFriendPostCard);
        textViewEmailFriend = view.findViewById(R.id.textViewEmailPostCard);
            mImageProfileViewFriend = view.findViewById(R.id.imageFriendProfile);
            mImageViewAddFriend = view.findViewById(R.id.imageViewAddFriend);




        }
    }
}
