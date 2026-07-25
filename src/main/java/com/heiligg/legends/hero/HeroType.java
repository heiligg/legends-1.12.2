package com.heiligg.legends.hero;

public enum HeroType {
    IRON_MAN("iron_man", "Iron Man", 120),
    SPIDER_MAN("spider_man", "Spider-Man", 100),
    FLASH("flash", "Flash", 110);

    public final String id;
    public final String displayName;
    public final int maxEnergy;

    HeroType(String id, String displayName, int maxEnergy) {
        this.id = id;
        this.displayName = displayName;
        this.maxEnergy = maxEnergy;
    }
}
