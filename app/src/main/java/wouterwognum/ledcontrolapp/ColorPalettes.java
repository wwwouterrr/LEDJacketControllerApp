package wouterwognum.ledcontrolapp;

public enum ColorPalettes
{
    Cloud ("Cloud"),
    Lava ("Lava"),
    Party ("Party"),
    Ocean ("Ocean"),
    Forest ("Forest"),
    Heat ("Heat"),
    Rainbow ("Rainbow"),
    RainbowStripe ("RainbowStripe"),
    PurpleAndGreen ("Purple/Green"),
    PurpleAndWhite ("Purple/White"),
    BlackAndWhiteStriped ("B/W Striped"),
    BlackWhitePurpleStriped ("B/W/Purple Striped"),
    Red ("Red"),
    Yellow ("Yellow"),
    Green ("Green"),
    Blue ("Blue");

    private String friendlyName;

    private ColorPalettes(String friendlyName){
        this.friendlyName = friendlyName;
    }

    @Override public String toString(){
        return friendlyName;
    }

}