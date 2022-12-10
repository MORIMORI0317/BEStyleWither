package net.morimori0317.bestylewither.config;

public interface BESConfig {
    BESConfig DEFAULT = new DefaultBESConfig();

    boolean isEnableBounceBlueWitherSkull();

    boolean isEnableMoreInertialBlueWitherSkull();

    boolean isEnableShootMoreBlueWitherSkull();

    boolean isEnableSpinAndWhiteSummon();

    boolean isEnableExplodeByHalfHealth();

    boolean isEnableExplodeByDie();

    boolean isEnableChargeAttack();

    boolean isEnableDoubleHealth();

    boolean isEnableMaintainWeakenedState();
}
