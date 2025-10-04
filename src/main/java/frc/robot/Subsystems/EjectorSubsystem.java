package frc.robot.Subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class EjectorSubsystem extends SubsystemBase{
    private SparkMax ejectorMotor;

    public EjectorSubsystem() {
        ejectorMotor = new SparkMax(Constant.CanIdConstants.ejectorID, MotorType.kBrushless);
    }

    public void driveEjector(double speed, double runPercentAsDecimal) {
        runPercentAsDecimal = Math.max(runPercentAsDecimal, 0.1);
        runPercentAsDecimal = Math.min(runPercentAsDecimal, 1.0);
        ejectorMotor.set(speed * runPercentAsDecimal);
    }
}
