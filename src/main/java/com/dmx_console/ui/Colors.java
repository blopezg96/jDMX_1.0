package com.dmx_console.ui;

public enum Colors {

    BG_BASE ("#0a0a0f"),
    BG_PANEL ("#12121a"),
    BG_CARD ("#1a1a28"),
    BG_ELEVATED ( "#22223a"),
    ACCENT ("#f0a500"),
    ACCENT_DIM  ("#7a5200"),
    TEXT_PRIMARY ("#e8e8f0"),
    TEXT_MUTED ("#6a6a8a"),
    RED_CH  ("#ff3a3a"),
    GREEN_CH ("#3aff6a"),
    BLUE_CH ("#3a8aff"),
    WHITE_CH ("#e8e8e8"),
    YELLOW_CH ("#ffe53a"),
    STROBE_CH ("#aa3aff"),
    DIM_CH ("#ff8c3a");

    private final String hex;

    Colors(String hex){
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }
}
