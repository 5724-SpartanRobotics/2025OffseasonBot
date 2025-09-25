package frc.robot.Subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class EjectorSubsystem extends SubsystemBase{
    private SparkMax ejectorMotor;

    public EjectorSubsystem() {
        ejectorMotor = new SparkMax(Constant.CanIdConstants.ejectorID, MotorType.kBrushless);
    }

    public void driveEjector(double speed) {
        ejectorMotor.set(speed);
    }
}
