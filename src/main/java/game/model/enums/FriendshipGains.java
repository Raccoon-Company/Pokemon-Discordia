package game.model.enums;

import game.model.Pokemon;

public enum FriendshipGains {
    LEVEL_UP(5,3,2),
    FAINT_MINUS_30(-1,-1,-1),
    FAINT_PLUS_30(-5,-5,-10),
    BOSS_BATTLE(3,2,1),
    WALKING(1,1,1),
    VITAMIN(5,3,2),
    FRIEND_BERRY(10,5,2),
    CT(1,1,0),
    MASSAGE_6(30,30,30),
    MASSAGE_20(10,10,10),
    MASSAGE_74(5,5,5),
    ;

    private final int upTo100;
    private final int upTo200;
    private final int upTo255;

    FriendshipGains(int upTo100, int upTo200, int upTo255) {
        this.upTo100 = upTo100;
        this.upTo200 = upTo200;
        this.upTo255 = upTo255;
    }

    public int getUpTo100() {
        return upTo100;
    }

    public int getUpTo200() {
        return upTo200;
    }

    public int getUpTo255() {
        return upTo255;
    }

    public static int getGainsFromAction(FriendshipGains fg,int currentFriendship){
        if(currentFriendship <= 100){
            return fg.getUpTo100();
        }else if(currentFriendship <= 200){
            return fg.getUpTo200();
        }else{
            return fg.upTo255;
        }
    }
}
