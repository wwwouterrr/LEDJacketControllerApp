package wouterwognum.ledcontrolapp;

public enum EffectTypes
{
    Noise ("Noise"),
    Stars ("Stars"),
    Fire ("Fire"),
    ZigZag ("ZigZag");

    private String friendlyName;

    private EffectTypes(String friendlyName){
        this.friendlyName = friendlyName;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
