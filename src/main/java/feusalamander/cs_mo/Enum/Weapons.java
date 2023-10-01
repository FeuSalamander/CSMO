package feusalamander.cs_mo.Enum;

public enum Weapons {
    AK47("AK47", 15, WeaponTypes.T, 2900, "AK_47"),
    M4A1("M4A1", 15, WeaponTypes.AT, 2900, "M4A1"),
    UMP("UMP-45", 4, WeaponTypes.ALL, 1700, " "),
    KIT("Defuse Kit", 18, WeaponTypes.AT, 400, "Kit");
    public final String name;
    public final int slot;
    public final WeaponTypes type;
    public final int price;
    public final String id;
    Weapons(String name, int slot, WeaponTypes type, int price, String id){
        this.name = name;
        this.slot = slot;
        this.type = type;
        this.price = price;
        this.id = id;
    }
}
