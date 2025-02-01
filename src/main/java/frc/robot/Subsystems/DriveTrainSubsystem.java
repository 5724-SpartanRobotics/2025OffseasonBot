package frc.robot.Subsystems;

import com.ctre.phoenix6.hardware.Pigeon2;

import choreo.trajectory.SwerveSample;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Subsystems.Constant.CanIdConstants;
import frc.robot.Subsystems.Constant.DebugLevel;
import frc.robot.Subsystems.Constant.DebugSetting;
import frc.robot.Subsystems.Constant.DriveConstants;

public class DriveTrainSubsystem extends SubsystemBase {
    private final Field2d m_field = new Field2d();
    private final SwerveDriveOdometry swerveDriveOdometry;
    private final SwerveModule LF;
    private final SwerveModule RF;
    private final SwerveModule LB;
    private final SwerveModule RB;
    private final SwerveDriveKinematics swerveDriveKinematics;
    private Pose2d robotPose;
    private final SwerveModule[] modules;
    private Rotation2d lastUpdatedGyroHeading;
    private final Pigeon2 gyro;
    private final PIDController xController = new PIDController(10.0, 0.0, 0.0);
    private final PIDController yController = new PIDController(10.0, 0.0, 0.0);
    private final PIDController headingController = new PIDController(7.5, 0.0, 0.0);
    private final SwerveDrivePoseEstimator poseEstimator;

    public DriveTrainSubsystem() {
        gyro = new Pigeon2(CanIdConstants.PigeonID);
        UpdateGyro();
        headingController.enableContinuousInput(-Math.PI, Math.PI);

        LF = new SwerveModule(CanIdConstants.LFTurnMotor, CanIdConstants.LFDriveMotor, CanIdConstants.LFCanID, DriveConstants.LFOff, "LF");
        RF = new SwerveModule(CanIdConstants.RFTurnMotor, CanIdConstants.RFDriveMotor, CanIdConstants.RFCanID, DriveConstants.RFOff, "RF");
        LB = new SwerveModule(CanIdConstants.LBTurnMotor, CanIdConstants.LBDriveMotor, CanIdConstants.LBCanID, DriveConstants.LBOff, "LB");
        RB = new SwerveModule(CanIdConstants.RBTurnMotor, CanIdConstants.RBDriveMotor, CanIdConstants.RBCanID, DriveConstants.RBOff, "RB");

        swerveDriveKinematics = new SwerveDriveKinematics(
            DriveConstants.LFLocation, DriveConstants.RFLocation, DriveConstants.LBLocation, DriveConstants.RBLocation
        );

        robotPose = new Pose2d(new Translation2d(4.0, 5.0), new Rotation2d());

        SwerveModulePosition[] swerveInitialPositions = {
            LF.getPosition(), RF.getPosition(), LB.getPosition(), RB.getPosition()
        };

        swerveDriveOdometry = new SwerveDriveOdometry(
            swerveDriveKinematics, getGyroHeading(), swerveInitialPositions, robotPose
        );

        modules = new SwerveModule[]{LF, RF, LB, RB};

        SmartDashboard.putData("Field", m_field);

        // Matrix<N3, N1> zeroMatrix = new Matrix<>(Nat.N3(), Nat.N1());
        // zeroMatrix.fill(0.0);

        poseEstimator = new SwerveDrivePoseEstimator(swerveDriveKinematics, lastUpdatedGyroHeading, swerveInitialPositions, robotPose);
    }

    public Rotation2d getGyroHeading() {
        return lastUpdatedGyroHeading;
    }

    public double getGyroRate() {
        return gyro.getAngularVelocityZWorld().getValueAsDouble();
    }

    public SwerveDrivePoseEstimator getPoseEstimator() {
        return poseEstimator;
    }

    public void setGyroZero() {
        gyro.setYaw(0);
    }

    public void resetOdometry(Pose2d pose) {
        swerveDriveOdometry.resetPosition(
            lastUpdatedGyroHeading,
            new SwerveModulePosition[]{
                LF.getPosition(), RF.getPosition(), LB.getPosition(), RB.getPosition()
            },
            pose
        );
    }

    public void ZeroDriveSensors(Pose2d xy) {
        swerveDriveOdometry.resetPosition(
            lastUpdatedGyroHeading,
            new SwerveModulePosition[]{LF.getPosition(), RF.getPosition(), LB.getPosition(), RB.getPosition()},
            xy.times(-1)
        );
    }

    @Override
    public void periodic() {
        super.periodic();

        UpdateGyro();

        SwerveModulePosition[] positions = new SwerveModulePosition[]{
            LF.getPosition(), RF.getPosition(), LB.getPosition(), RB.getPosition()
        };

        robotPose = swerveDriveOdometry.update(
            getGyroHeading(),
            positions
        );

        m_field.setRobotPose(robotPose);

        if (DebugSetting.TraceLevel == DebugLevel.Swerve || DebugSetting.TraceLevel == DebugLevel.All) {
            SmartDashboard.putNumber("RobotPoseX", robotPose.getX());
            SmartDashboard.putNumber("RobotPoseY", robotPose.getY());
            LF.periodic();
            RF.periodic();
            LB.periodic();
            RB.periodic();
       }

       poseEstimator.update(getGyroHeading(), positions);
       SmartDashboard.putNumber("drivetrain estimated pose X", getPoseEstimator().getEstimatedPosition().getX());
       SmartDashboard.putNumber("drivetrain estimated pose Y", getPoseEstimator().getEstimatedPosition().getY());
    }

    @SuppressWarnings("removal")
    private void UpdateGyro() {
        lastUpdatedGyroHeading = Rotation2d.fromDegrees(-gyro.getAngle());
    }

    protected void _drive(Translation2d translation, double rotation) {
        SwerveModuleState[] swerveModStates = swerveDriveKinematics.toSwerveModuleStates(
            ChassisSpeeds.fromFieldRelativeSpeeds(
                translation.getX(), translation.getY(), rotation, getGyroHeading()
            )
        );

        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModStates, DriveConstants.maxRobotSpeedmps);

        LF.setDesiredState(swerveModStates[0]);
        RF.setDesiredState(swerveModStates[1]);
        LB.setDesiredState(swerveModStates[2]);
        RB.setDesiredState(swerveModStates[3]);
    }

    public void drive(SwerveModuleState[] swerveModStates) {
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModStates, DriveConstants.maxRobotSpeedmps);

        LF.setDesiredState(swerveModStates[0]);
        RF.setDesiredState(swerveModStates[1]);
        LB.setDesiredState(swerveModStates[2]);
        RB.setDesiredState(swerveModStates[3]);
    }

    public void drive(Translation2d translation, double rotation) {
        _drive(translation, rotation);
    }

    public void drive(double rotation) {
        _drive(new Translation2d(0, 0), rotation);
    }

    public void drive(Pose2d pose) {
        _drive(pose.getTranslation(), pose.getRotation().getRadians());
    }

    public void drive(Pose3d pose) {
        drive(pose.toPose2d());
    }

    public void drive() {
        _drive(new Translation2d(0, 0), 0);
    }

    public void simulationInit() {
        // Simulation initialization logic can go here
    }

    @Override
    public void simulationPeriodic() {
        LF.periodic();
        RF.periodic();
        LB.periodic();
        RB.periodic();
    }

    public Pose2d getPose() {
        return robotPose;
    }

    public void followTrajectory(SwerveSample sample) {
        Pose2d pose = getPose();

        ChassisSpeeds speeds = new ChassisSpeeds(
            sample.vx + xController.calculate(pose.getX(), sample.x),
            sample.vy + yController.calculate(pose.getY(), sample.y),
            sample.omega + headingController.calculate(pose.getRotation().getRadians(), sample.heading)
        );

        SwerveModuleState[] swerveModStates = swerveDriveKinematics.toSwerveModuleStates(speeds);

        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModStates, DriveConstants.maxRobotSpeedmps);

        LF.setDesiredState(swerveModStates[0]);
        RF.setDesiredState(swerveModStates[1]);
        LB.setDesiredState(swerveModStates[2]);
        RB.setDesiredState(swerveModStates[3]);
    }

    public void brake() {
        for (SwerveModule module : modules) {
            module.setDesiredState(new SwerveModuleState(0, module.getState().angle));
        }
    }
}
