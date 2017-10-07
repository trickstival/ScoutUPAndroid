package com.tcc.lucca.scoutup.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.lucca.scoutup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import com.tcc.lucca.scoutup.gerenciar.GrupoDAO;
import com.tcc.lucca.scoutup.gerenciar.ListViewAdapter;
import com.tcc.lucca.scoutup.gerenciar.SessaoDAO;
import com.tcc.lucca.scoutup.gerenciar.UsuarioDAO;
import com.tcc.lucca.scoutup.model.Grupo;
import com.tcc.lucca.scoutup.model.Sessao;
import com.tcc.lucca.scoutup.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class PerfilFragmentActivity extends Fragment {


    private UsuarioDAO usuarioDAO = UsuarioDAO.getInstance();
    private GrupoDAO grupoDAO = GrupoDAO.getInstance();
    private SessaoDAO sessaoDAO = SessaoDAO.getInstance();

    private View view;

    private FirebaseUser firebaseUser;
    private Usuario usuarioDatabase;
    private Grupo grupoDatabase;
    private Sessao sessaoDatabase;

    private ListView listViewInfo;
    private ListView listViewAmigos;
    private ListView listViewEspec;

    private Button btnLogout;
    private Button btnEditar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_perfil_fragment, container, false);

        initComponents(root);

        initData();

        return root;
    }

    private void initComponents(View container) {
        this.view = container;
        listViewInfo = container.findViewById(R.id.listViewInformacoes);
        listViewAmigos = container.findViewById(R.id.listViewAmigos);
        listViewEspec = container.findViewById(R.id.listViewEspecialidades);

    }

    private void initData() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        usuarioDAO.buscarPorIdUser(uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                Usuario user = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1).toObject(Usuario.class);
                setUsuarioDatabase(user);

            }
        });



    }

    private void atualizarInfoPerfil() {

//        String uidGrupo = usuarioDatabase.getGrupo();
//        grupoDatabase =(Grupo) grupoDAO.buscarPorId(uidGrupo);
//
//        String uidSecao = usuarioDatabase.getSessao();
//        sessaoDatabase = (Sessao) sessaoDAO.buscarPorId(uidSecao);

        List<String> info = new ArrayList<>();
        info.add("SCOUT UP");

        info.add(usuarioDatabase.getNome());
        info.add(usuarioDatabase.getEmail());

        if (grupoDatabase != null) {
            info.add(grupoDatabase.getNome());
        }
        if (sessaoDatabase != null) {
            info.add(sessaoDatabase.getNome());
        }

        ListViewAdapter adapter = new ListViewAdapter(getContext(), info);
        if (listViewInfo != null) {
            listViewInfo.setAdapter(adapter);
        }
    }

    public void setUsuarioDatabase(Usuario usuarioDatabase) {
        this.usuarioDatabase = usuarioDatabase;
        atualizarInfoPerfil();
        atualizarAmigos();
        atualizarEspecialidades();
    }


    private void atualizarAmigos() {

//        listViewAmigos = getView().findViewById(R.id.listViewAmigos);
//
//        HashMap<String, Amigo> amigosMap = usuarioDatabase.getAmigos();
//
//        List<Amigo> amigos = new ArrayList<>(amigosMap.values());
//
//
//        AmigoListAdapter adapter = new AmigoListAdapter(getContext(), amigos);
//
//        listViewAmigos.setAdapter(adapter);

    }

    private void atualizarEspecialidades() {

    }


}

