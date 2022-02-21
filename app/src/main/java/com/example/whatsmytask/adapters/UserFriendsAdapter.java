package com.example.whatsmytask.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsmytask.R;
import com.example.whatsmytask.models.Friend;
import com.example.whatsmytask.models.User;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.FriendsProvider;
import com.example.whatsmytask.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFriendsAdapter extends FirestoreRecyclerAdapter<User, UserFriendsAdapter.ViewHolder> {
    Context context;
    UsersProvider mUserProvider;
    FriendsProvider mFriendProvider;
    AuthProvider mAuthProvider;

    public UserFriendsAdapter(FirestoreRecyclerOptions<User> options, Context context){
        super(options);
        this.context = context;
        mUserProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NotNull UserFriendsAdapter.ViewHolder holder, int position,@NotNull User user) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        final String friendId = document.getId();
        getFriendInfo(friendId, holder);
    }

    private void getFriendInfo(String friendId, ViewHolder holder) {
        mUserProvider.getUser(friendId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // convierte la consulta en el objeto indicado
                User user = documentSnapshot.toObject(User.class);

                if(documentSnapshot.exists()){
                    if (documentSnapshot.contains("userName")){
                        String userName = documentSnapshot.getString("userName");
                        holder.textViewNameFriend.setText(userName.toUpperCase());
                    }
                    if(documentSnapshot.contains("email")){
                        String email = documentSnapshot.getString("email");
                        holder.textViewEmailFriend.setText(email);
                    }
                    if (documentSnapshot.contains("imageProfile")){
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if (imageProfile != null){
                            if (!imageProfile.isEmpty()){
                                Picasso.with(context).load(imageProfile).into(holder.mImageProfileViewFriend);
                            }
                        }
                    }
                }
                holder.mImageViewDeleteFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showConfirmDeleteFriend(user);
                    }
                });
            }
        });
    }

    // Confirmar por alert dialog eliminar amigo
    private void showConfirmDeleteFriend(User user) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.remove_friend_dialog, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        // quitamos el background del dialog para pasarle el custom_border
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView imgVCloseRemove;
        CircleImageView cImgVFriend;
        TextView txtVFriendName , txtVTitleRemoveFriendDialog;
        Button btnCancelRemove, btnRemove;

        imgVCloseRemove = view.findViewById(R.id.imgVCloseRemoveFriend);
        cImgVFriend = view.findViewById(R.id.cImgVFriend);
        txtVFriendName = view.findViewById(R.id.txtFriendName);
        txtVTitleRemoveFriendDialog = view.findViewById(R.id.txtVTitleRemoveFriendDialog);
        btnCancelRemove = view.findViewById(R.id.btnCancelRemove);
        btnRemove = view.findViewById(R.id.btnRemoveFriend);

        txtVTitleRemoveFriendDialog.setText("DELETE FRIEND");
        Picasso.with(context).load(user.getImageProfile()).into(cImgVFriend);
        txtVFriendName.setText(user.getUserName());

        imgVCloseRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCancelRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnRemove.setText("DELETE");
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteFriend(user.getId());
            }
        });

    }

    // Metodo para borrar amigo
    private void deleteFriend(String friendId) {

        mFriendProvider.deleteFriendByIdUser1(mAuthProvider.getUid(), friendId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    mFriendProvider.deleteFriendByIdUser2(friendId, mAuthProvider.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(context, "Friend deleted", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context, "Error erasing friend", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }else{
                    Toast.makeText(context, "Error erasing friend", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user_friends ,parent , false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewNameFriend;
        TextView textViewEmailFriend;
        ImageView mImageProfileViewFriend;
        ImageView mImageViewDeleteFriend;

        public ViewHolder(View view){
            super(view);
            textViewNameFriend = view.findViewById(R.id.textViewUserFriend);
            textViewEmailFriend = view.findViewById(R.id.textViewUserFriendEmail);
            mImageProfileViewFriend = view.findViewById(R.id.imageUserFriendProfile);
            mImageViewDeleteFriend = view.findViewById(R.id.imageViewDeleteUserFriend);

            mFriendProvider = new FriendsProvider();
            mAuthProvider = new AuthProvider();
        }
    }
}
