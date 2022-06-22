package net.morimori0317.bestylewither.entity;

public interface BEWitherBoss {
    int getWitherDeathTime();

    float getWitherDeathTime(float delta);

    int getDestroyBlocksTick();

    void setDestroyBlocksTick(int tick);

    int getChargeCoolDown();

    void setChargeCoolDown(int tick);

    int getClientCharge();

    void setClientCharge(int charge);

    float getClientCharge(float delta);
}
