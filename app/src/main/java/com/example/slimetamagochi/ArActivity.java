package com.example.slimetamagochi;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private ModelRenderable mon_modele;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        ModelRenderable.builder()
                .setSource(this, Uri.parse("slime.sfb"))
                .build()
                .thenAccept(renderable -> mon_modele = renderable);

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (mon_modele == null) {
                        return;
                    }
                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                        return;
                    }


                    //On créé le point d'encrage du modèle 3d
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    //On attache ensuite notre modèle au point d'encrage
                    TransformableNode Node = new TransformableNode(arFragment.getTransformationSystem());
                    Node.setParent(anchorNode);
                    Node.setRenderable(mon_modele);
                    Node.select();


                }
        );
    }
}
