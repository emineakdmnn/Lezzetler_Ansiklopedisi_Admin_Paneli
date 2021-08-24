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
import com.emineakduman.lezzetansiklopedisi.viewholder.KategoriViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {
    Button btn_kategori_ekle;
    MaterialEditText edtKategoriAdi;
    FButton btnSec,btnYukle;
    public static final int PICK_IMAGE_REQUEST=71;
    Uri kaydetmeUrisi;
    FirebaseDatabase database;
    DatabaseReference kategoriYolu;
    FirebaseStorage storage;
    StorageReference resimYolu;
    FirebaseRecyclerAdapter<Kategori, KategoriViewHolder> adapter;
    RecyclerView recyler_kategori;
    RecyclerView.LayoutManager layoutManager;
    Kategori yeniKategori;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database=FirebaseDatabase.getInstance();
        kategoriYolu=database.getReference("Kategori");
        storage = FirebaseStorage.getInstance();
        resimYolu=storage.getReference();


        recyler_kategori=findViewById(R.id.recycler_kategori);
        recyler_kategori.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyler_kategori.setLayoutManager(layoutManager);


        btn_kategori_ekle = findViewById(R.id.btn_kategori_ekle);

        btn_kategori_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategoriPenceresiEkleGoster();
            }
        });

        kategoriYukle();
    }

    private void kategoriYukle() {
        FirebaseRecyclerOptions<Kategori> secenekler = new FirebaseRecyclerOptions.Builder<Kategori>().setQuery(kategoriYolu,Kategori.class).build();
        adapter= new FirebaseRecyclerAdapter<Kategori, KategoriViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull KategoriViewHolder kategoriViewHolder, int i, @NonNull Kategori kategori) {

                kategoriViewHolder.txtKategoriAdi.setText(kategori.getAd());
                Picasso.with(getBaseContext()).load(kategori.getResim()).into(kategoriViewHolder.imageView);

                Kategori tiklandiginda = kategori;
                kategoriViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // her bir satıra tıklandığında ne yapsın
                        Intent turler = new Intent(MainActivity.this,TurlerActivity.class);
                        turler.putExtra("KategoriId",adapter.getRef(position).getKey());
                        startActivity(turler);
                    }
                });


            }

            @NonNull
            @Override
            public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.kategori_satiri_ogesi,parent,false);
                return new KategoriViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyler_kategori.setAdapter(adapter);
    }

    private void kategoriPenceresiEkleGoster() {
       final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Yeni Kategori Ekle");
        builder.setMessage("Lütfen Bilgilerinizi Giriniz..");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View yeni_kategori_ekle_Penceresi = layoutInflater.inflate(R.layout.yeni_kategori_ekleme_penceresi,null);

        edtKategoriAdi= yeni_kategori_ekle_Penceresi.findViewById(R.id.edtKategoriAdi);
        btnSec= yeni_kategori_ekle_Penceresi.findViewById(R.id.btnSec);
        btnYukle= yeni_kategori_ekle_Penceresi.findViewById(R.id.btnYukle);

        btnSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimSec();
            }
        });

        btnYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();
            }
        });

        builder.setView(yeni_kategori_ekle_Penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //ategoriyi veri tabınına aktarma
                if(yeniKategori !=null){
                    kategoriYolu.push().setValue(yeniKategori);
                    Toast.makeText(MainActivity.this,yeniKategori.getAd()+" Kategorisi Eklendi!",Toast.LENGTH_LONG).show();

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
         final ProgressDialog mDialog =new  ProgressDialog(this);
            mDialog.setMessage("Yükleniyor...");
            mDialog.show();

            String resimAdi = UUID.randomUUID().toString();
            StorageReference resimDosyasi = resimYolu.child("resimler/"+resimAdi);
            resimDosyasi.putFile(kaydetmeUrisi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Resim Yüklendi..",Toast.LENGTH_LONG).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            //resmi veritabanına katarma
                            yeniKategori =  new Kategori(edtKategoriAdi.getText().toString(),uri.toString());

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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
            btnSec.setText("SEÇİLDİ");

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
            kategoriGuncellemePenceresiGoster(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));

        }
       else if(item.getTitle().equals("Sil")){
            //Silme
            kategoriSil(adapter.getRef(item.getOrder()).getKey());

        }
        return super.onContextItemSelected(item);
    }

    private void kategoriGuncellemePenceresiGoster(String key, Kategori item) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Yeni Kategori Ekle");
        builder.setMessage("Lütfen Bilgilerinizi Giriniz..");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View yeni_kategori_ekle_Penceresi = layoutInflater.inflate(R.layout.yeni_kategori_ekleme_penceresi,null);

        edtKategoriAdi= yeni_kategori_ekle_Penceresi.findViewById(R.id.edtKategoriAdi);
        btnSec= yeni_kategori_ekle_Penceresi.findViewById(R.id.btnSec);
        btnYukle= yeni_kategori_ekle_Penceresi.findViewById(R.id.btnYukle);

        btnSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimSec();
            }
        });

        btnYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimDegis(item);
            }
        });

        builder.setView(yeni_kategori_ekle_Penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //kategoriyi güncelleme
                item.setAd(edtKategoriAdi.getText().toString());
                kategoriYolu.child(key).setValue(item);
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

    private void resimDegis(Kategori item) {
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
                    Toast.makeText(MainActivity.this,"Resim Güncellendi..",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
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

    private void kategoriSil(String key) {
        kategoriYolu.child(key).removeValue();
    }
}