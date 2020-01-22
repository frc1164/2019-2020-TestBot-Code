/*----------------------------------------------------------------------------*/ /* Copyright (c) 2017-2018 FIRST. All Rights Reserved. */ /* Open Source Software - may be modified and shared by FRC teams. The code */ /* must be accompanied by the FIRST BSD license file in the root directory of */ /* the project. */ /*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick; 
import edu.wpi.first.wpilibj.TimedRobot; 
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SPI; 
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX; 
import com.kauailabs.navx.frc.AHRS;



/**
* This is a demo program showing the use of the RobotDrive class, specifically
* it contains the code necessary to operate a robot with tank drive.
*/

 public class Robot extends TimedRobot {

// To name Motors, Joysticks, Solenoids etc. they must be addressed in "TimedRobot" to prevent local variables
 public static VictorSPX LeftMotorFront, LeftMotorRear, RightMotorFront, RightMotorRear;
 public static Joystick DriverStick;
 public static Solenoid Solenoid_0, Solenoid_1, Solenoid_6, Solenoid_7, RaiseBall, LowerBall;
 public static Encoder leftEncoder, rightEncoder;
 public static AHRS m_Navx;
 public static XboxController OperatorStick;
 public static TalonSRX EndEffector; 
 public static NetworkTable table;
 public static NetworkTableEntry tx, ty, ta;
 

 @Override
 public void robotInit() {
   //Solenoids, motors and Joystick are adressed by their respective port(s) 
   //PCM is port 05 on the CANbus

   
   Solenoid_0 = new Solenoid(5,0);//Low/High Gear
   Solenoid_7 = new Solenoid(5,7);
   Solenoid_1 = new Solenoid(5,1);//Low/High Gear
   Solenoid_6 = new Solenoid(5,6);
   RaiseBall = new Solenoid(5,2);
   LowerBall = new Solenoid(5,5);

   //Turn Off Solenoids at start
   RaiseBall.set(false);
   LowerBall.set(true);

   //Speed controllers
   LeftMotorFront = new VictorSPX(2);
   LeftMotorRear = new VictorSPX(1);
   RightMotorFront = new VictorSPX(13);
   RightMotorRear = new VictorSPX(14);
   EndEffector = new TalonSRX(0);

   //Controllers
   DriverStick = new Joystick(0);
   OperatorStick = new XboxController(1);
   
   //Encoders
   leftEncoder = new Encoder(0,1);
   rightEncoder = new Encoder(2,3);

   //Navx
   m_Navx = new AHRS(SPI.Port.kMXP);

   //Limelight network tables
   table = NetworkTableInstance.getDefault().getTable("limelight");
   tx = table.getEntry("tx");
   ty = table.getEntry("ty");
   ta = table.getEntry("ta");


  }

 @Override
 public void teleopPeriodic() {
   
   //If statements shift gears with button press
   
    if (DriverStick.getRawButton(7) == true) {
      // Button B
      Solenoid_1.set(false);
      Solenoid_0.set(false);
      Solenoid_6.set(true);
      Solenoid_7.set(true);

      SmartDashboard.putBoolean("Button 7 Pushed",DriverStick.getRawButton(7));
    }

    if (DriverStick.getRawButton(8) == true) {
      // Button A
      Solenoid_6.set(false);
      Solenoid_7.set(false);
      Solenoid_1.set(true);
      Solenoid_0.set(true);
      SmartDashboard.putBoolean("Button 8 Pushed",DriverStick.getRawButton(8));
    }

    if (OperatorStick.getRawButton(6) == true) {
      RaiseBall.set(false);
      LowerBall.set(true);

      SmartDashboard.putBoolean("Lower End Effector", OperatorStick.getRawButton(6));
    }

    if (OperatorStick.getRawButton(5) == true) {
      RaiseBall.set(true);
      LowerBall.set(false);

      SmartDashboard.putBoolean("Raise End Effector", OperatorStick.getRawButton(5));
    }
    


    //Invert Joystick as necessary
    double Axis_1 = DriverStick.getRawAxis(1);
    double Axis_2 = DriverStick.getRawAxis(2);
    double Axis_3 = DriverStick.getRawAxis(3);
    double OperatorLTrigger = OperatorStick.getRawAxis(2);
    double OperatorRTrigger = OperatorStick.getRawAxis(3);
    OperatorRTrigger = -OperatorRTrigger;

    //Control EndEffector
    EndEffector.set(ControlMode.PercentOutput, OperatorLTrigger);
    EndEffector.set(ControlMode.PercentOutput, OperatorRTrigger);

    //Inverts motors as necessary
    LeftMotorRear.setInverted(true);
    LeftMotorFront.setInverted(true);

    //DeadBands
    Axis_2 = (Math.abs(Axis_2) <= 0.25) ? 0 : Axis_2; 
    Axis_1 = (Math.abs(Axis_1) <= 0.1) ? 0 : Axis_1; 

    //Speed value for Left Motors
    double Speed_L = ((-Axis_3*Axis_1) - Axis_2);
    double Speed_R = ((-Axis_3*Axis_1) + Axis_2);

    //read limelight values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    //post limelight values to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);

    //Displays Joystick values on Smart Dashboard
    SmartDashboard.putNumber("Left Motors", Speed_L);
    SmartDashboard.putNumber("Right Motors", Speed_R);
    SmartDashboard.putNumber("Speed Axis Variable", Axis_1);
    SmartDashboard.putNumber("Turning Variable", Axis_2);
    SmartDashboard.putNumber("overall Speed variable", Axis_3);

    //Motors set to speed determined by Joystick values
    LeftMotorFront.set(ControlMode.PercentOutput, Speed_L);
    LeftMotorRear.set(ControlMode.PercentOutput, Speed_L);
    RightMotorFront.set(ControlMode.PercentOutput, Speed_R);
    RightMotorRear.set(ControlMode.PercentOutput, Speed_R);

    //Read Encoders in SmartDashboard
    SmartDashboard.putNumber("Left Encoder",leftEncoder.get());
    SmartDashboard.putNumber("Right Encoder",rightEncoder.get());
    SmartDashboard.putNumber("End Effector Velocity", EndEffector.getSelectedSensorVelocity());

    //Read NavX in SmartDashboard
    SmartDashboard.putNumber("X-axis Acceleration", m_Navx.getRawAccelX());
    SmartDashboard.putNumber("Y-axis Acceleration", m_Navx.getRawAccelY());
    SmartDashboard.putNumber("X-axis Degrees", m_Navx.getRawGyroX()); 
    SmartDashboard.putNumber("Y-axis Degrees", m_Navx.getRawGyroY()); 

    //Read OperatorStick buttons
    SmartDashboard.putBoolean("Green Button Pressed", OperatorStick.getRawButton(1));
    SmartDashboard.putBoolean("Red Button Pressed", OperatorStick.getRawButton(2));
    SmartDashboard.putBoolean("Blue Button Pressed", OperatorStick.getRawButton(3));
    SmartDashboard.putBoolean("Yellow Button Pressed", OperatorStick.getRawButton(4));
    SmartDashboard.putBoolean("Back Button Pressed", OperatorStick.getRawButton(7));
    SmartDashboard.putBoolean("Start Button Pressed", OperatorStick.getRawButton(8));
    

  } 
}