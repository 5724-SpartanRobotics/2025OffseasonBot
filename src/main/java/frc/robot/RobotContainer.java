package frc.robot;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Subsystems.Constant;
import frc.robot.Subsystems.DriveTrainSubsystem;
import frc.robot.Subsystems.EjectorSubsystem;
import frc.robot.commands.TeleopSwerve;

public class RobotContainer {
    private final DriveTrainSubsystem drive;
    private final EjectorSubsystem ejector;
    private final CommandJoystick drivestick;

    private Trigger jb_ZeroGyro;
    private Trigger jb_Rotate180;

    public RobotContainer() {
        this.drive = new DriveTrainSubsystem();
        this.ejector = new EjectorSubsystem();
        this.drivestick = new CommandJoystick(0);

        this.jb_ZeroGyro = this.drivestick.button(Constant.ControllerConstants.ButtonMap.GyroZero);
        this.jb_Rotate180 = this.drivestick.button(Constant.ControllerConstants.ButtonMap.Rotate180);

        configureButtonBindings();

        drive.setDefaultCommand(new TeleopSwerve(drive, drivestick));

        ejector.setDefaultCommand(new InstantCommand(() -> {
            ejector.driveEjector(drivestick.getRawAxis(Constant.EjectorConstants.joystickAxis));
        }));
    }

    private void configureButtonBindings() {
        jb_ZeroGyro.onTrue(new InstantCommand(() -> {
            drive.setGyroZero();
        }));
        jb_Rotate180.onTrue(new InstantCommand(() -> {
            drive.drive(-Math.PI);
        }));
    }
}
