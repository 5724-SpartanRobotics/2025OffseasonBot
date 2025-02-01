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
    // Choreo trajectory support
    // private final Optional<Trajectory<SwerveSample>> trajectory = Choreo.loadTrajectory("myTrajectory");

    private final PowerDistribution powerDistribution = new PowerDistribution(0, ModuleType.kCTRE);;

    private final RobotContainer m_robotContainer;

    public Robot() {
        DataLogManager.start();
        DriverStation.startDataLog(DataLogManager.getLog());

        m_robotContainer = new RobotContainer();
    }

    @Override
    public void robotInit() {
        SmartDashboard.putNumber("joystickDeadband", 0.1);
        SmartDashboard.putNumber("joystickZDeadband", 0.5);

        // trajectory.ifPresent(t -> Logger.recordOutput("Choreo/Trajectory", t.samples().toArray(new SwerveSample[0])));

        PortForwarder.add(5800, "photonvision.local", 5800);
        PortForwarder.add(5801, "limelight.local", 5801);

        // Pass DriveTrainSubsystem and Joystick to TeleopSwerve
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

        // SwerveModuleState[] states = new SwerveModuleState[] {
        //         new SwerveModuleState(), 
        //         new SwerveModuleState(),
        //         new SwerveModuleState(),
        //         new SwerveModuleState()
        // };
        // Logger.recordOutput("SwerveModuleStates", states);

        // Pose3d poseA = new Pose3d(); 
        // Pose3d poseB = new Pose3d(); 
        // Logger.recordOutput("PoseA", poseA);
        // Logger.recordOutput("PoseArray", new Pose3d[] {poseA, poseB});
    }

    @Override
    public void autonomousInit() {
        // This might be from ChatGpt.1!
        // trajectory.ifPresent(t -> {
        //     Optional<Pose2d> initialPose = t.getInitialPose(isRedAlliance());
        //     initialPose.ifPresent(pose -> drive.resetOdometry(pose));
        // });
    }

    public static boolean isRedAlliance() {
        return DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red;
    }  
}
