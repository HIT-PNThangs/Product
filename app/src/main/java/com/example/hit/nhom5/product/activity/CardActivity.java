package com.example.hit.nhom5.product.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hit.nhom5.product.R;
import com.example.hit.nhom5.product.adapter.CartAdapter;
import com.example.hit.nhom5.product.databinding.ActivityCardBinding;
import com.example.hit.nhom5.product.fragment.PurchaseFragment;
import com.example.hit.nhom5.product.model.Cart;
import com.example.hit.nhom5.product.model.User;
import com.example.hit.nhom5.product.my_interface.CartOnClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardActivity extends AppCompatActivity {
    ActivityCardBinding binding;
    CartAdapter cartAdapter;
    long tongTien;
    List<Cart> order = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
        recyclerCart();
    }

    private void recyclerCart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference =
                database.getReference()
                        .child("Users")
                        .child(Objects.requireNonNull(auth.getUid()));

        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);

                if (user != null) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    binding.recyCart.setLayoutManager(linearLayoutManager);
                    cartAdapter = new CartAdapter(this, user.getCarts());
                    binding.recyCart.setAdapter(cartAdapter);

                    NumberFormat numberFormatter = new DecimalFormat("###,###,###VND");

                    cartAdapter.setCartOnClick(new CartOnClick() {
                        @Override
                        public void onSelectClick(Cart cart) {
                            tongTien += (long) cart.getSoLuong() * cart.getProduct().getRealPrice();
                            binding.total.setText(numberFormatter.format(tongTien));

                            order.add(cart);
                        }

                        @Override
                        public void onDestroyClick(Cart cart) {
                            if(tongTien < (long) cart.getSoLuong() * cart.getProduct().getRealPrice()) tongTien = 0;
                            else if(tongTien <= 0) tongTien = 0;
                            else tongTien -= (long) cart.getSoLuong() * cart.getProduct().getRealPrice();

                            binding.total.setText(numberFormatter.format(tongTien));

                            order.remove(cart);
                        }

                        @Override
                        public void onPlusClick(Cart cart, boolean isChecked) {
                            if(isChecked) {
                                tongTien += (long) cart.getProduct().getRealPrice();
                                binding.total.setText(numberFormatter.format(tongTien));
                            }
                        }

                        @Override
                        public void onMinusClick(Cart cart, boolean isChecked) {
                            if(isChecked) {
                                if(cart.getSoLuong() > 1) tongTien -= (tongTien - (long) cart.getProduct().getRealPrice() * cart.getSoLuong());
                                if(tongTien <= (long) cart.getProduct().getRealPrice())
                                    tongTien = (long) cart.getProduct().getRealPrice();
                                else tongTien -= (long) cart.getProduct().getRealPrice();
                                binding.total.setText(numberFormatter.format(tongTien));
                            }
                        }
                    });
                }
            }
        });
    }

    private void setListener() {
        binding.btnBuy.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout4, new PurchaseFragment());
            fragmentTransaction.commit();
        });

        binding.back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        finish();
    }
}