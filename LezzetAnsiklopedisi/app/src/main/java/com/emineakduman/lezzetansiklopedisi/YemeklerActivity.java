package com.emineakduman.lezzetansiklopedisi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.emineakduman.lezzetansiklopedisi.arayuz.ItemClickListener;
import com.emineakduman.lezzetansiklopedisi.model.Turler;
import com.emineakduman.lezzetansiklopedisi.model.Yemek;
import com.emineakduman.lezzetansiklopedisi.viewholder.TurlerViewHolder;
import com.emineakduman.lezzetansiklopedisi.viewholder.YemekViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class YemeklerActivity extends AppCompatActivity {
    Button btn_yemek_ekle;
    MaterialEditText edtYemekAdi;
    MaterialEditText edtMalzemeler;
    MaterialEditText edtYapilisi;
  //  MaterialEditText edtIzlemeLinki;
    MaterialEditText edtPufNoktasi;
    FButton btnYemekSec,btnYemekYukle;
    public static final int PICK_IMAGE_REQUEST=71;
    Uri kaydetmeUrisi;
    FirebaseStorage storage;
    StorageReference resimYolu;
    FirebaseDatabase database;
    DatabaseReference yemekYolu;
    Yemek yeniYemek;
    String turId="";
    FirebaseRecyclerAdapter<Yemek, YemekViewHolder> adapter;
    RecyclerView recyler_yemekler;
    RecyclerView.LayoutManager layoutManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yemekler);

        database=FirebaseDatabase.getInstance();
        yemekYolu=database.getReference("Yemekler");
        storage = FirebaseStorage.getInstance();
        resimYolu=storage.getReference();

        recyler_yemekler=findViewById(R.id.recycler_yemekler);
        recyler_yemekler.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyler_yemekler.setLayoutManager(layoutManager);

        btn_yemek_ekle = findViewById(R.id.btn_yemek_ekle);
        btn_yemek_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yemekEklemePenceresiGoster();
            }
        });

        if (getIntent() != null)
        {
            turId=getIntent().getStringExtra("TurId");
        }
        if(!turId.isEmpty())
        {
           yemekleriYukle(turId);
        }
    }

    private void yemekleriYukle(String turId) {

        Query filtrele = yemekYolu.orderByChild("turid").equalTo(turId);
        FirebaseRecyclerOptions<Yemek> secenekler = new FirebaseRecyclerOptions.Builder<Yemek>().setQuery(filtrele,Yemek.class).build();
        adapter= new FirebaseRecyclerAdapter<Yemek, YemekViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull YemekViewHolder yemekViewHolder, int i, @NonNull Yemek yemek) {

                yemekViewHolder.txtYemekAdi.setText(yemek.getYemekadi());
                yemekViewHolder.txtYemekMalzemesi.setText(yemek.getMalzemeler());
                yemekViewHolder.txtYemekYapilisi.setText(yemek.getYapilis());
                yemekViewHolder.txtYemekPufNoktasi.setText(yemek.getPufnoktasi());
              //  yemekViewHolder.txtYemekIzlemeLinki.setText(yemek.getIzlemelinki());
                Picasso.with(getBaseContext()).load(yemek.getResim()).into(yemekViewHolder.imageView);

             final Yemek tiklandiginda = yemek;
                yemekViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // her bir satıra tıklandığında ne yapsın
                        Intent turler = new Intent(YemeklerActivity.this,YemeklerActivity.class);
                        turler.putExtra("TurId",adapter.getRef(position).getKey());
                        startActivity(turler);
                    }
                });
            }

            @NonNull
            @Override
            public YemekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.yemek_satiri_ogesi,parent,false);
                return new YemekViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyler_yemekler.setAdapter(adapter);
    }

    private void yemekEklemePenceresiGoster() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(YemeklerActivity.this);
        builder.setTitle("Yeni Yemek Ekle");
        builder.setMessage("Lütfen Bilgilerinizi Giriniz..");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View yeni_yemek_ekle_Penceresi = layoutInflater.inflate(R.layout.yeni_yemek_ekleme_penceresi,null);

        edtYemekAdi= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtYemekAdi);
        edtMalzemeler= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtMalzemeler);
        edtYapilisi= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtYapilis);
       // edtIzlemeLinki= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtIzlemeLinki);
        edtPufNoktasi= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtPufNoktasi);
        btnYemekSec= yeni_yemek_ekle_Penceresi.findViewById(R.id.btnYemekSec);
        btnYemekYukle= yeni_yemek_ekle_Penceresi.findViewById(R.id.btnYemekYukle);

        btnYemekSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimSec();
            }
        });

        btnYemekYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();
            }
        });

        builder.setView(yeni_yemek_ekle_Penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //yemeği veri tabınına aktarma
               if(yeniYemek !=null){
                    yemekYolu.push().setValue(yeniYemek);
                    Toast.makeText(YemeklerActivity.this,yeniYemek.getYemekadi()+" Kategorisi Eklendi!",Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();

            }
        });

        builder.setNegativeButton("VAZGEÇ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sonra kodlanacak
                dialog.dismiss();

            }
        });
        builder.show();
    }

    private void resimYukle() {
        if(kaydetmeUrisi != null){
            ProgressDialog mDialog =new  ProgressDialog(this);
            mDialog.setMessage("Yükleniyor...");
            mDialog.show();

            String resimAdi = UUID.randomUUID().toString();
            StorageReference resimDosyasi = resimYolu.child("resimler/"+resimAdi);
            resimDosyasi.putFile(kaydetmeUrisi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(YemeklerActivity.this,"Resim Yüklendi..",Toast.LENGTH_LONG).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //resmi veritabanına katarma
                           yeniYemek =  new Yemek();
                           yeniYemek.setYemekadi(edtYemekAdi.getText().toString());
                           yeniYemek.setMalzemeler(edtMalzemeler.getText().toString());
                           yeniYemek.setYapilis(edtYapilisi.getText().toString());
                           yeniYemek.setPufnoktasi(edtPufNoktasi.getText().toString());
                          // yeniYemek.setIzlemelinki(edtIzlemeLinki.getText().toString());
                           yeniYemek.setTurid(turId);
                           yeniYemek.setResim(uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(YemeklerActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("% " +progress+" yüklendi");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() !=null){
            kaydetmeUrisi = data.getData();
            btnYemekSec.setText("SEÇİLDİ");

        }

    }

    private void resimSec() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Resim Seç"),PICK_IMAGE_REQUEST);
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("Güncelle")){
            //Güncelleme
            yemekGuncellemePenceresiGoster(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }
        else if(item.getTitle().equals("Sil")){
            //Silme
            yemekSil(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void yemekGuncellemePenceresiGoster(String key, Yemek item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(YemeklerActivity.this);
        builder.setTitle("Yemeği Güncelle");
        builder.setMessage("Lütfen Bilgilerinizi Giriniz..");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View yeni_yemek_ekle_Penceresi = layoutInflater.inflate(R.layout.yeni_yemek_ekleme_penceresi,null);

        edtYemekAdi= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtYemekAdi);
        edtMalzemeler= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtMalzemeler);
        edtYapilisi= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtYapilis);
        edtPufNoktasi= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtPufNoktasi);
      //  edtIzlemeLinki= yeni_yemek_ekle_Penceresi.findViewById(R.id.edtIzlemeLinki);
        btnYemekSec= yeni_yemek_ekle_Penceresi.findViewById(R.id.btnYemekSec);
        btnYemekYukle= yeni_yemek_ekle_Penceresi.findViewById(R.id.btnYemekYukle);

        btnYemekSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimSec();
            }
        });

        btnYemekYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimDegis(item);
            }
        });

        builder.setView(yeni_yemek_ekle_Penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //kategoriyi güncelleme
                item.setYemekadi(edtYemekAdi.getText().toString());
                item.setMalzemeler(edtMalzemeler.getText().toString());
                item.setYapilis(edtYapilisi.getText().toString());
                item.setPufnoktasi(edtPufNoktasi.getText().toString());
             //   item.setIzlemelinki(edtIzlemeLinki.getText().toString());
                yemekYolu.child(key).setValue(item);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("VAZGEÇ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sonra kodlanacak
                dialog.dismiss();

            }
        });
        builder.show();
    }

    private void resimDegis(Yemek item) {

        if(kaydetmeUrisi != null){
            final ProgressDialog mDialog =new  ProgressDialog(this);
            mDialog.setMessage("Yükleniyor...");
            mDialog.show();
            String resimAdi = UUID.randomUUID().toString();
            StorageReference resimDosyasi = resimYolu.child("resimler/"+resimAdi);
            resimDosyasi.putFile(kaydetmeUrisi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(YemeklerActivity.this,"Resim Güncellendi..",Toast.LENGTH_LONG).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //resmi güncelleme
                            item.setResim(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(YemeklerActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    mDialog.setMessage("% " +progress+" yüklendi");
                }
            });
        }
    }

    private void yemekSil(String key) {
        yemekYolu.child(key).removeValue();
    }
}