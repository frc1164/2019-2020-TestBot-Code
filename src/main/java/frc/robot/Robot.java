/*----------------------------------------------------------------------------*/ /* Copyright (c) 2017-2018 FIRST. All Rights Reserved. */ /* Open Source Software - may be modified and shared by FRC teams. The code */ /* must be accompanied by the FIRST BSD license file in the root directory of */ /* the project. */ /*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick; 
import edu.wpi.first.wpilibj.TimedRobot; 
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

/**
* This is a demo program showing the use of the RobotDrive class, specifically
* it contains the code necessary to operate a robot with tank drive.
*/

 public class Robot extends TimedRobot {

// To name Motors, Joysticks, Solenoids etc. they must be addressed in "TimedRobot" to prevent local variables
 public static VictorSPX LeftMotorFront, LeftMotorRear, RightMotorFront, RightMotorRear;
 public static Joystick m_Stick;
 public static Solenoid num_0, num_1, num_2, num_3, num_4, num_5, num_6, num_7;

 @Override
 public void robotInit() {
   //Solenoids, motors and Joystick are adressed by their respective port(s) 

   
   num_0 = new Solenoid(5, 0);//Low/High Gear
   num_7 = new Solenoid(5, 7);
   num_1 = new Solenoid(5, 1);//Low/High Gear
   num_6 = new Solenoid(5, 6);
  

   //PCM is port 05 on the CANbus

   //Speed controllers
   LeftMotorFront = new VictorSPX(2);
   LeftMotorRear = new VictorSPX(1);
   RightMotorFront = new VictorSPX(13);
   RightMotorRear = new VictorSPX(14);
   m_Stick = new Joystick(0);
  }

 @Override
 public void teleopPeriodic() {
   
   //If statements shift gears with button press
   
    if (m_Stick.getRawButton(8) == true){
      // Button B
      num_1.set(false);
      num_0.set(false);
      num_6.set(true);
      num_7.set(true);

      SmartDashboard.putNumber("Button 8 Pushed", 1);
    }

    if (m_Stick.getRawButton(9) == true){
      // Button A
      num_6.set(false);
      num_7.set(false);
      num_1.set(true);
      num_0.set(true);
      SmartDashboard.putNumber("Button 9 Pushed", 1);
    }
    

    //Invert Joystick asnecessayry
    double Axis_1 = m_Stick.getRawAxis(1);
    double Axis_2 = m_Stick.getRawAxis(2);
    double Axis_3 = m_Stick.getRawAxis(3);

    //Inverts motors as necessary
    LeftMotorRear.setInverted(true);
    LeftMotorFront.setInverted(true);

    //DeadBandxdgg5
    Axis_2 = (Math.abs(Axis_2) <= 0.25) ? 0 : Axis_2; 
    Axis_1 = (Math.abs(Axis_1) <= 0.1) ? 0 : Axis_1; 

    //Speed value for Left Motors
    double Speed_L = ((-Axis_3*Axis_1) - Axis_2);
    double Speed_R = ((-Axis_3*Axis_1) + Axis_2);

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
    

  } 
}