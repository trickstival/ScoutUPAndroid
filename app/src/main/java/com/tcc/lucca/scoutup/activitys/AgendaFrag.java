package com.tcc.lucca.scoutup.activitys;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.tcc.lucca.scoutup.R;
import com.tcc.lucca.scoutup.gerenciar.AtividadeDAO;
import com.tcc.lucca.scoutup.gerenciar.AtividadeListAdapter;
import com.tcc.lucca.scoutup.gerenciar.UsuarioDAO;
import com.tcc.lucca.scoutup.model.Amigo;
import com.tcc.lucca.scoutup.model.Atividade;
import com.tcc.lucca.scoutup.model.MapAtividadePH;
import com.tcc.lucca.scoutup.model.Tipo;
import com.tcc.lucca.scoutup.model.Usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AgendaFrag extends Fragment {

    private Usuario usuario;
    private FloatingActionButton fab;
    private ListView listView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        initData();


    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_atividades, container, false);

        listView = root.findViewById(R.id.listViewAgenda);

        TextView tvAgenda = root.findViewById(R.id.textView);
        Typeface type = Typeface.createFromAsset(getContext().getAssets(), "font/ClaireHandRegular.ttf");
        tvAgenda.setTypeface(type);

        fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), AdicionarAtividadeActivity.class);
                startActivity(intent);




            }
        });

        fab.hide();


        return root;
    }


    private void initData() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String uid = firebaseUser.getUid();

        final UsuarioDAO usuarioDAO = UsuarioDAO.getInstance();

        usuarioDAO.buscarPorId(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    Usuario user = dataSnapshot.getValue(Usuario.class);

                    if (user.getTipo().equals(Tipo.devolveString(Tipo.escotista))) {

                        if(user.getSecao() != null){

                            fab.show();



                        }

                    }
                    setUsuario(user);
                    carregarAtividades();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void carregarAtividades() {

        final AtividadeDAO dao = AtividadeDAO.getInstance();

        dao.listar(usuario.getGrupo(), usuario.getSecao().get("chave")).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                List<String> ids = new ArrayList<String>();


                for (DataSnapshot data:dataSnapshot.getChildren()){

                    dataSnapshot.getValue().toString();

                    MapAtividadePH ativ = dataSnapshot.getValue(MapAtividadePH.class);

                    if(ativ!=null){
                        Log.d("Tag", " ph "+ativ.getChavePH());
                        Log.d("Tag", " ativ "+ativ.getChaveAtividade());
                        ids.add(ativ.getChaveAtividade());

                    }
                }

                if (ids != null ) {

                    final List<Atividade> atividades = new ArrayList<>();

                    for (int i = 0; i < ids.size(); i++) {

                        String chaveAtiv = ids.get(i);

                        dao.setReferencia("atividade");


                        dao.buscarPorId(chaveAtiv).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d("Tag", " data "+dataSnapshot.getValue().toString());

                                Atividade atividade = dataSnapshot.getValue(Atividade.class);
                                Log.d("Tag", " ph "+dataSnapshot.getValue().toString());

                                atividades.add(atividade);
                                AtividadeListAdapter adapter = new AtividadeListAdapter(getContext(), atividades);
                                listView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
