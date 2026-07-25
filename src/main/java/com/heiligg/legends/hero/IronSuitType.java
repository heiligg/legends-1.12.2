package com.heiligg.legends.hero;

/**
 * Full Iron Man suit lineup. Each suit is a complete armor set with unique powers.
 */
public enum IronSuitType {
    MARK_I("mark_i", "mark_i", "Mark I", 90, 1, false, 0.6F,
            "G Flamethrower | F Rocket Jump | V Smoke Screen",
            "Prototype cave suit — no sustained flight"),
    MARK_III("iron_man", "iron_man", "Mark III", 160, 2, true, 1.0F,
            "G Repulsor | F Flight | V Unibeam",
            "Classic red-and-gold combat suit"),
    MARK_V("mark_v", "mark_v", "Mark V", 120, 3, true, 1.25F,
            "G Quick Repulsor | F Flight | V Pulse Wave",
            "Lightweight briefcase suit — fast, fragile"),
    MARK_VII("mark_vii", "mark_vii", "Mark VII", 180, 2, true, 1.15F,
            "G Repulsor | F Flight | V Flare Burst",
            "Pod-deployed suit with countermeasures"),
    MARK_XLII("mark_xlii", "mark_xlii", "Mark XLII", 170, 3, true, 1.1F,
            "G Repulsor | F Flight | V Auto-Repair",
            "Pre-assemble suit — regenerates durability"),
    WAR_MACHINE("war_machine", "war_machine", "War Machine", 200, 2, true, 0.85F,
            "G Missile Barrage | F Hover Flight | V Minigun Spin",
            "Heavy weapons platform"),
    HULKBUSTER("hulkbuster", "hulkbuster", "Hulkbuster", 220, 1, true, 0.55F,
            "G Power Fist | F Heavy Hover | V Ground Pound",
            "Anti-Hulk modular bunker suit"),
    MARK_L("mark_l", "mark_l", "Mark L", 240, 4, true, 1.35F,
            "G Nano-Repulsor | F Flight | V Nanite Surge",
            "Bleeding Edge nanotech — peak performance"),
    STEALTH("stealth", "stealth", "Stealth Suit", 140, 2, true, 1.2F,
            "G Suppressed Shot | F Silent Flight | V Cloak",
            "Low-observability infiltration suit");

    /** Item / recipe registry prefix (Mark III keeps iron_man for compatibility). */
    public final String id;
    /** Armor texture name under textures/models/armor/. */
    public final String textureName;
    public final String displayName;
    public final int maxEnergy;
    public final int rechargeRate;
    public final boolean canFly;
    public final float flightSpeed;
    public final String controls;
    public final String lore;

    IronSuitType(String id, String textureName, String displayName, int maxEnergy, int rechargeRate,
                 boolean canFly, float flightSpeed, String controls, String lore) {
        this.id = id;
        this.textureName = textureName;
        this.displayName = displayName;
        this.maxEnergy = maxEnergy;
        this.rechargeRate = rechargeRate;
        this.canFly = canFly;
        this.flightSpeed = flightSpeed;
        this.controls = controls;
        this.lore = lore;
    }

    public String getRegistryPrefix() {
        return id;
    }

    public String pieceName(String piece) {
        return id + "_" + piece;
    }
}
