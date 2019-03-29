package zork;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import scala.concurrent.duration.Duration;
import zork.enviromnment.Device;

public class DeviceTest {
  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void teardown() {
    TestKit.shutdownActorSystem(system, Duration.Zero(), true);
    system = null;
  }

  @Test
  public void testReplyWithLatestTemperatureReading() {
    TestKit probe = new TestKit(system);
    ActorRef deviceActor = system.actorOf(Device.props("groupId", "deviceId"));

    deviceActor.tell(new Device.RecordTemperature(1L, 24.0), probe.getRef());
    assertEquals(1L, probe.expectMsgClass(Device.TemperatureRecorded.class).requestId);

    deviceActor.tell(new Device.ReadTemperature(2L), probe.getRef());
    Device.RespondTemperature response1 = probe.expectMsgClass(Device.RespondTemperature.class);
    assertEquals(2L, response1.requestId);
    assertEquals(Optional.of(24.0), response1.value);

    deviceActor.tell(new Device.ReadTemperature(4L), probe.getRef());
    Device.RespondTemperature response2 = probe.expectMsgClass(Device.RespondTemperature.class);
    assertEquals(4L, response2.requestId);
    assertEquals(Optional.of(55.0), response2.value);
  }

  @Test
  public void testReplyWithEmptyReadingIfNoTemperatureIsKnown() {
    TestKit probe = new TestKit(system);
    ActorRef deviceActor = system.actorOf(Device.props("group", "device"));
    deviceActor.tell(new Device.ReadTemperature(42L), probe.getRef());
    Device.RespondTemperature respond = probe.expectMsgClass(Device.RespondTemperature.class);
    assertEquals(42L, respond.requestId);
    assertEquals(Optional.empty(), respond.value);
  }
}
