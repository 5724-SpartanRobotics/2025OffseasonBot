package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import frc.robot.Subsystems.Constant;
import frc.robot.Subsystems.EjectorSubsystem;

public class EjectorCommand extends Command {
    private CommandJoystick controller;
    private EjectorSubsystem ejector;

    /**
     * Drive Controller
     * 
     * @param ejector    The drive train subsystem
     * @param controller A joystick
     */
    public EjectorCommand(EjectorSubsystem ejector, CommandJoystick controller) {
        this.controller = controller;
        this.ejector = ejector;
        addRequirements((SubsystemBase) ejector);
    }

    @Override
    public void execute() {
        double speed = controller.getRawAxis(Constant.EjectorConstants.forwardAxis) - controller.getRawAxis(Constant.EjectorConstants.backAxis);
        ejector.driveEjector(-speed);

    }

}