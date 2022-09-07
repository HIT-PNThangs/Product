package com.example.hit.nhom5.product.my_interface;

import com.example.hit.nhom5.product.model.Cart;

public interface CartOnClick {
    void onSelectClick(Cart cart);
    void onDestroyClick(Cart cart);
    void onPlusClick(Cart cart, boolean isChecked);
    void onMinusClick(Cart cart, boolean isChecked);
}
