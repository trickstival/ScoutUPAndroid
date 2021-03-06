package com.tcc.lucca.scoutup.backgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.firebase.geofire.core.GeoHashQuery;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tcc.lucca.scoutup.activitys.LoginActivity;
import com.tcc.lucca.scoutup.activitys.MainActivity;
import com.tcc.lucca.scoutup.gerenciar.UsuarioDAO;
import com.tcc.lucca.scoutup.model.Tipo;
import com.tcc.lucca.scoutup.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executor;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class LoginClass {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private Context context;
    private Usuario usuario;
    private Uri mDownloadUrl = null;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private boolean isEscotista;
    private static final int RC_SIGN_IN = 8888;



    public LoginClass(final Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };


    }



    public void loginCredentials(String email, String senha) {

        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    firebaseUser = mAuth.getCurrentUser();
                    Intent main = new Intent(context, MainActivity.class);
                    context.startActivity(main);
                    ((Activity) context).finish();



                } else {

                    Toast.makeText(context, "Email ou senha erradas", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, final boolean checked) {
        isEscotista = checked;

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        verificarUsuarioCadastrado();
                    }
                });
    }

    public void firebaseAuthWithFacebook(AccessToken accessToken, final boolean checked) {
        isEscotista = checked;

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        verificarUsuarioCadastrado();
                    }
                });

    }

    public void verificarUsuarioCadastrado() {

        try{

            String uid = mAuth.getCurrentUser().getUid();


            usuarioDAO.buscarPorId(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.exists()){
                        cadastro();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Intent main = new Intent(context, MainActivity.class);
            context.startActivity(main);

            ((Activity) context).finish();


        }catch (Exception e){

            Toast.makeText(context, "Nao logado", Toast.LENGTH_LONG).show();



        }





    }

    private void cadastro() {


        Usuario user = new Usuario();
        user.setNome(mAuth.getCurrentUser().getDisplayName());
        user.setEmail(mAuth.getCurrentUser().getEmail());
        //uploadFile();

        if (isEscotista) {
            user.setTipo(Tipo.devolveString(Tipo.escotista));
        }else{

            user.setTipo(Tipo.devolveString(Tipo.escoteiro));

        }
        usuarioDAO.adicionar(user);
        Intent main = new Intent(context, MainActivity.class);
        context.startActivity(main);

        ((Activity) context).finish();




    }


    private void uploadFile() {

        for (UserInfo profile : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {

            Log.d("TAG", "entrou no for");


            if (profile.getProviderId().equals("facebook.com")) {


                String facebookUserId = profile.getUid();

                Log.d("TAG", "face" + facebookUserId);

                String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=180width=180";

                Log.d("TAG", "url" + photoUrl);

                Uri uri = Uri.parse(photoUrl);


                uploadFromUri(uri);
            }

        }



    }

    private void uploadFromUri(Uri fileUri) {

        Uri uploadUri = Uri.fromFile(new File(fileUri.toString()));

        Log.d("TAG", "uploadFromUri:src:" + fileUri.toString());


        final StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("fotoPerfil").child(uploadUri.getLastPathSegment());

        Log.d("TAG", "uploadFromUri:dst:" + photoRef.getPath());

        photoRef.putFile(uploadUri).addOnSuccessListener((Executor) this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("TAG", "uploadFromUri:onSuccess");

                        mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();

                    }
                })
                .addOnFailureListener((Executor) this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.w("TAG", "uploadFromUri:onFailure", exception);

                        mDownloadUrl = null;

                    }
                });
    }


    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseAuth.AuthStateListener getmAuthListener() {
        return mAuthListener;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }





}
