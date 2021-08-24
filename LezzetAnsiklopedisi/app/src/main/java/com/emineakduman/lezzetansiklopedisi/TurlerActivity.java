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
import com.emineakduman.lezzetansiklopedisi.model.Kategori;
import com.emineakduman.lezzetansiklopedisi.model.Turler;
import com.emineakduman.lezzetansiklopedisi.viewholder.KategoriViewHolder;
import com.emineakduman.lezzetansiklopedisi.viewholder.TurlerViewHolder;
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

public class TurlerActivity extends AppCompatActivity {

    Button btn_tur_ekle;
    MaterialEditText edtTurAdi;
    FButton btnTurSec,btnTurYukle;
    public static final int PICK_IMAGE_REQUEST=71;
    Uri kaydetmeUrisi;
    FirebaseStorage storage;
    StorageReference resimYolu;
    FirebaseDatabase database;
    DatabaseReference turYolu;
    FirebaseRecyclerAdapter<Turler, TurlerViewHolder> adapter;
    Turler yeniTur;
    String kategoriId="";
    RecyclerView recyler_turler;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turler);

        database=FirebaseDatabase.getInstance();
        turYolu=database.getReference("Turler");
        storage = FirebaseStorage.getInstance();
        resimYolu=storage.getReference();

        recyler_turler=findViewById(R.id.recycler_turler);
        recyler_turler.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyler_turler.setLayoutManager(layoutManager);



        btn_tur_ekle= findViewById(R.id.btn_tur_ekle);

        btn_tur_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turEklemePenceresiGoster();
            }
        });

        if (getIntent() != null)
        {
            kategoriId=getIntent().getStringExtra("KategoriId");
        }
        if(!kategoriId.isEmpty())
        {
            turleriYukle(kategoriId);
        }


    }

    private void turleriYukle(String kategoriId) {

        Query filtrele = turYolu.orderByChild("kategoriid").equalTo(kategoriId);
        FirebaseRecyclerOptions<Turler> secenekler = new FirebaseRecyclerOptions.Builder<Turler>().setQuery(filtrele,Turler.class).build();
        adapter= new FirebaseRecyclerAdapter<Turler, TurlerViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull TurlerViewHolder turlerViewHolder, int i, @NonNull Turler turler) {

                turlerViewHolder.txtTurAdi.setText(turler.getAd());
                Picasso.with(getBaseContext()).load(turler.getResim()).into(turlerViewHolder.imageView);

                Turler tiklandiginda = turler;
                turlerViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // her bir satıra tıklandığında ne yapsın
                       Intent turler = new Intent(TurlerActivity.this,YemeklerActivity.class);
                        turler.putExtra("TurId",adapter.getRef(position).getKey());
                        startActivity(turler);
                    }
                });


            }

            @NonNull
            @Override
            public TurlerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.tur_satiri_ogesi,parent,false);
                return new TurlerViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyler_turler.setAdapter(adapter);

    }

    private void turEklemePenceresiGoster() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(TurlerActivity.this);
        builder.setTitle("Yeni Tür Ekle");
        builder.setMessage("Lütfen Bilgilerinizi Giriniz..");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View yeni_tur_ekle_Penceresi = layoutInflater.inflate(R.layout.yeni_tur_ekleme_penceresi,null);

        edtTurAdi= yeni_tur_ekle_Penceresi.findViewById(R.id.edtTurAdi);
        btnTurSec= yeni_tur_ekle_Penceresi.findViewById(R.id.btnTurSec);
        btnTurYukle= yeni_tur_ekle_Penceresi.findViewById(R.id.btnTurYukle);

        btnTurSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimSec();
            }
        });

        btnTurYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();
            }
        });

        builder.setView(yeni_tur_ekle_Penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ategoriyi veri tabınına aktarma
                if(yeniTur !=null){
                    turYolu.push().setValue(yeniTur);
                    Toast.makeText(TurlerActivity.this,yeniTur.getAd()+" Kategorisi Eklendi!",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TurlerActivity.this,"Resim Yüklendi..",Toast.LENGTH_LONG).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //resmi veritabanına katarma
                            yeniTur =  new Turler(edtTurAdi.getText().toString(),uri.toString(),kategoriId);

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(TurlerActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("Güncelle")){
            //Güncelleme
            turGuncellemePenceresiGoster(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }
        else if(item.getTitle().equals("Sil")){
            //Silme
            turSil(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void turGuncellemePenceresiGoster(String key, Turler item) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(TurlerActivity.this);
        builder.setTitle("Yeni Kategori Ekle");
        builder.setMessage("Lütfen Bilgilerinizi Giriniz..");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View yeni_tur_ekle_Penceresi = layoutInflater.inflate(R.layout.yeni_tur_ekleme_penceresi,null);

        edtTurAdi= yeni_tur_ekle_Penceresi.findViewById(R.id.edtTurAdi);
        btnTurSec= yeni_tur_ekle_Penceresi.findViewById(R.id.btnTurSec);
        btnTurYukle= yeni_tur_ekle_Penceresi.findViewById(R.id.btnTurYukle);

        btnTurSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimSec();
            }
        });

        btnTurYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimDegis(item);
            }
        });

        builder.setView(yeni_tur_ekle_Penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //kategoriyi güncelleme
                item.setAd(edtTurAdi.getText().toString());
                turYolu.child(key).setValue(item);
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

    private void resimDegis(Turler item) {

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
                    Toast.makeText(TurlerActivity.this,"Resim Güncellendi..",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TurlerActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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

    private void turSil(String key) {
        turYolu.child(key).removeValue();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() !=null){
            kaydetmeUrisi = data.getData();
            btnTurSec.setText("SEÇİLDİ");

        }
    }

    private void resimSec() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Resim Seç"),PICK_IMAGE_REQUEST);
    }
}