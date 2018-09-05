package com.icsseseguridad.locationsecurity.events;


public class OnUserCheck {

    public final int position;
    public final boolean checked;

    public OnUserCheck(int position, boolean checked) {
        this.position = position;
        this.checked = checked;
    }
}
