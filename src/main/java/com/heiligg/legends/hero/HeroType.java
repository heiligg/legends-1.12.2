package com.heiligg.legends.hero;

public enum HeroType {
    IRON_MAN("iron_man", "Iron Man", 160, 2),
    SPIDER_MAN("spider_man", "Spider-Man", 140, 2),
    FLASH("flash", "Flash", 150, 3),
    CAPTAIN_AMERICA("captain_america", "Captain America", 145, 2);

    public final String id;
    public final String displayName;
    public final int maxEnergy;
    /** Energy regained per armor piece each second. */
    public final int rechargeRate;

    HeroType(String id, String displayName, int maxEnergy, int rechargeRate) {
        this.id = id;
        this.displayName = displayName;
        this.maxEnergy = maxEnergy;
        this.rechargeRate = rechargeRate;
    }
}
