package org.firstinspires.ftc.teamcode.lib.hardware.base;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.lib.movement.MyPosition;
import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.ExpansionHubMotor;
import org.openftc.revextensions2.RevBulkData;
import org.openftc.revextensions2.RevExtensions2;

import java.text.DecimalFormat;

import static org.firstinspires.ftc.teamcode.lib.movement.MyPosition.AngleWrap;
import static org.firstinspires.ftc.teamcode.lib.util.GlobalVars.*;

//@TeleOp
public class Robot extends OpMode{

  private boolean isAuto = true;

  public DecimalFormat df = new DecimalFormat("###.###");

  private RevBulkData revExpansionMasterBulkData;

  private ExpansionHubEx revMaster;
  // used in future if you need bulk reads from the other hub
  private ExpansionHubEx revSlave;

  private RevMotor[] motors;

  public DriveTrain dt = new DriveTrain();

  //public FtcDashboard dashboard = FtcDashboard.getInstance();
  //public TelemetryPacket packet = new TelemetryPacket();

  @Override
  public void init() {

    RevExtensions2.init();

    revMaster = hardwareMap.get(ExpansionHubEx.class,"Expansion Hub 2");
    //revSlave = hardwareMap.get(ExpansionHubEx.class,"Expansion Hub 5");

    motors = new RevMotor[]{new RevMotor((ExpansionHubMotor) hardwareMap.get("fl"),true), new RevMotor((ExpansionHubMotor) hardwareMap.get("fr"),true), new RevMotor((ExpansionHubMotor) hardwareMap.get("bl"),true), new RevMotor((ExpansionHubMotor) hardwareMap.get("br"),true)};

    dt.initMotors(motors);
    dt.initGyro(hardwareMap.get(BNO055IMU.class, "imu"));

  }

  @Override
  public void loop() {

    getRevBulkData();

    if(!isAuto){
      getGamepads(gamepad1, gamepad2);
    }

    if(roboState != RobotStates.FINISHED) {
      dt.applyMovement();
    }

    worldAngle_rad = Double.parseDouble(df.format(AngleWrap(dt.getGyroRotation(AngleUnit.RADIANS))));

    MyPosition.PosCalcNiceArnav(
        dt.fr.getCurrentPosition(),
        dt.bl.getCurrentPosition());

    worldXPosition = Double.parseDouble(df.format(worldXPosition));
    worldYPosition = Double.parseDouble(df.format(worldYPosition));


    updateAutoState();
    updateAtTarget();

    //telemetry.addLine("positions set!");

    telemetry.addLine("wx: " + worldXPosition);
    telemetry.addLine("wy: " + worldYPosition);
    telemetry.addLine("wa: " + Math.toDegrees(worldAngle_rad));
    telemetry.addLine("");
    telemetry.addLine("auto: " + auto);
   // telemetry.addLine("r: " + dt.fr.getCurrentPosition());
   // telemetry.addLine("a: " + dt.bl.getCurrentPosition());
    telemetry.addLine("");
    telemetry.addLine("auto state: " + autoState);
    telemetry.addLine("");
    telemetry.addLine("robot state: " + roboState);
    //telemetry.addLine("strafe const: " + strafeConstant);

    telemetry.update();

   /* packet.put("wx", worldXPosition);
    packet.put("wy", worldYPosition);
    packet.put("wa", Math.toDegrees(worldAngle_rad));
    packet.put("auto", auto);
    packet.put("autostate", autoState);
    packet.put("robot state", roboState);*/

    //dashboard.sendTelemetryPacket(packet);

  }

  public void getGamepads(Gamepad main, Gamepad aux){

    mainGp = main;
    auxGp = aux;

  }

  private void updateAtTarget(){
    if(((worldXPosition >= xTarget -mTolerance) && (worldXPosition <= xTarget+mTolerance)) && ((worldYPosition >= yTarget -mTolerance) && (worldYPosition <= yTarget+mTolerance)) && ((Math.toDegrees(worldAngle_rad) >= aTarget - aTolerance) && (Math.toDegrees(worldAngle_rad) <= aTarget +aTolerance))){
      roboState = RobotStates.AT_TARGET;
    } else if(roboState == RobotStates.AT_TARGET){
      roboState = RobotStates.MOVING_TO_TARGET;
    }
  }

  private void updateAutoState(){

    if(roboState == RobotStates.AT_TARGET){

      roboState = RobotStates.STOPPED;
      auto++;

    }
  }


  public void isAuto(boolean isAuto){
    this.isAuto = isAuto;
  }

  /**
   * Gets all the data from the expansion hub in one command to increase loop times
   */
  public void getRevBulkData() {
//        boolean needToPollMaster = !AutoFeeder.canPollMasterAtLowerRate ||
//            currTimeMillis-lastUpdateMasterTime > 300;
//        if(needToPollMaster){
    RevBulkData newDataMaster;
    try{
      newDataMaster = revMaster.getBulkInputData();
      if(newDataMaster != null){
        revExpansionMasterBulkData = newDataMaster;
      }
    }catch(Exception e){
      //don't set anything if we get an exception
    }


    for(RevMotor revMotor : motors) {
      if (revMotor == null) {
        continue;
      }
      if (revExpansionMasterBulkData != null) {
        revMotor.setEncoderReading(revExpansionMasterBulkData.getMotorCurrentPosition(revMotor.myMotor));
      }
    }

  }
}