package frc.robot;

import org.littletonrobotics.junction.LoggedRobot;

import edu.wpi.first.net.PortForwarder;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends LoggedRobot {
    private final PowerDistribution powerDistribution = new PowerDistribution(0, ModuleType.kCTRE);

    private final RobotContainer m_robotContainer;

    public Robot() {
        // DataLogManager.start();
        // DriverStation.startDataLog(DataLogManager.getLog());

        m_robotContainer = new RobotContainer();
    }

    @Override
    public void robotInit() {
        SmartDashboard.putNumber("joystickDeadband", 0.1);
        SmartDashboard.putNumber("joystickZDeadband", 0.5);
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();

        if (powerDistribution != null) {
            SmartDashboard.putNumber("PDP Voltage", powerDistribution.getVoltage());
            SmartDashboard.putNumber("PDP Total Current", powerDistribution.getTotalCurrent());
            SmartDashboard.putNumber("PDP Total Power", powerDistribution.getTotalPower());
            SmartDashboard.putNumber("PDP Temperature", powerDistribution.getTemperature());
        }
    }

    @Override
    public void autonomousInit() {
    }

    public static boolean isRedAlliance() {
        return DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red;
    }  
}
