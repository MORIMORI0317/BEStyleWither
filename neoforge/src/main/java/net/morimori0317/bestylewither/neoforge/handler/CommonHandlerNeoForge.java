package net.morimori0317.bestylewither.neoforge.handler;

import net.morimori0317.bestylewither.BEStyleWither;
import net.morimori0317.bestylewither.neoforge.networking.BSWPacketsNeoForge;
import net.morimori0317.bestylewither.networking.BSWPackets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = BEStyleWither.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonHandlerNeoForge {

    @SubscribeEvent
    public static void registerPayload( RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(BSWPacketsNeoForge.PROTOCOL_VERSION);
        registrar.playBidirectional(BSWPackets.WHITHER_CHARGE_TYPE, BSWPackets.WITHER_CHARGE_CODEC,
                new DirectionalPayloadHandler<>(BSWPacketsNeoForge::handleWitherChargeDataOnClient, BSWPacketsNeoForge::handleWitherChargeDataOnServer));
    }

}
