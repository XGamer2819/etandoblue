/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;


@Autonomous(name="cameraautonomous", group="Linear OpMode")

public class cameraautonomous extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.

    private DcMotor leftFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightFrontDrive;
    private DcMotor rightBackDrive;







    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera
    private static final String TFOD_MODEL_FILE = "rednearbackdrop.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
            "Red Left",
            "Red Middle",
            "Red Right"
    };

    public static TfodProcessor tfod;


    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;




    //init motors, servo, camera and color sensor
    private void initDcMotors() {
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "m4");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "m3");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "m1");
        rightBackDrive = hardwareMap.get(DcMotor.class, "m2");
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

    }


    public void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()


                .setModelFileName(TFOD_MODEL_FILE)


                .setModelLabels(LABELS)


                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }


        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

    }   // end method initTfod()

    public String telemetryTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());


            return recognition.getLabel();
        }   // end for() loop
        return "nothing";
    }   // end method telemetryTfod()



    //stop drive
    private void driveStop() {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        leftFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightFrontDrive.setPower(0);
        rightBackDrive.setPower(0);

    }

    private void drive(String direction, double runtimeInseconds,
                       double leftFrontPower, double rightFrontPower,
                       double leftBackPower, double rightBackPower) {
        ElapsedTime runtime = new ElapsedTime();
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);
        runtime.reset();
        if (direction == "Forward") {
            leftFrontDrive.setPower(leftFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightFrontDrive.setPower(rightFrontPower);
            rightBackDrive.setPower(rightBackPower);

        }
        if (direction == "Backward") {
            leftFrontDrive.setPower(-leftFrontPower);
            leftBackDrive.setPower(-leftBackPower);
            rightFrontDrive.setPower(-rightFrontPower);
            rightBackDrive.setPower(-rightBackPower);

        }
        if (direction == "Left") {
            leftFrontDrive.setPower(-leftFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightFrontDrive.setPower(rightFrontPower);
            rightBackDrive.setPower(-rightBackPower);

        }
        if (direction == "Right") {
            leftFrontDrive.setPower(leftFrontPower);
            leftBackDrive.setPower(-leftBackPower);
            rightFrontDrive.setPower(-rightFrontPower);
            rightBackDrive.setPower(rightBackPower);
        }
        if (direction == "TurnRight") {
            leftFrontDrive.setPower(leftFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightFrontDrive.setPower(-rightFrontPower);
            rightBackDrive.setPower(-rightBackPower);
        }
        if (direction == "TurnLeft") {
            leftFrontDrive.setPower(-leftFrontPower);
            leftBackDrive.setPower(-leftBackPower);
            rightFrontDrive.setPower(rightFrontPower);
            rightBackDrive.setPower(rightBackPower);
        }

        while (opModeIsActive() && (runtime.seconds() < runtimeInseconds)) {
            telemetry.update();

        }
    }




    boolean cameraDetected = false;
    boolean ihateleft = false;
    String label = "";
    private void motordrive() {
        if (cameraDetected == false) {
            label = telemetryTfod();
            telemetryTfod();
            telemetry.addData("camera", label);
            telemetry.update();
        }

        if (label == "Red Middle") {
                cameraDetected = true;
                leftFrontDrive.setPower(0.3);
                rightFrontDrive.setPower(0.3);
                leftBackDrive.setPower(0.3);
                rightBackDrive.setPower(0.3);
                telemetry.update();
        }

    }




    @Override
    public void runOpMode() throws InterruptedException {
        initDcMotors();
        initTfod();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        boolean startdriveflag = false;
        boolean preRedflag = false;


        // run until the end of the match (driver presses STOP)
        if (opModeIsActive()) {

            while (opModeIsActive()) {

                telemetry.update();

                motordrive();

                telemetry.update();

                if (label == "Red Middle") {
                    driveStop();
                    drive("Backward", 0.025, 0.5, 0.5, 0.5, 0.5);
                    drive("Backward", 0.025, 0.5, 0.5, 0.5, 0.5);
                    driveStop();
                    startdriveflag = true;
                }
               //right works
                if (label == "Red Right" && startdriveflag) {
                    drive("Backward", 0.2, 0.2, 0.2, 0.2, 0.2);
                    drive("TurnLeft", 0.825, 0.5, 0.5, 0.5, 0.5);
                    drive("Backward", 1.0, 0.3, 0.3, 0.3, 0.3);
                    drive("TurnLeft", 0.725, 0.5, 0.5, 0.5, 0.5);
                    drive("Backward", 1.75, 0.3, 0.3, 0.3, 0.3);
                    drive("Right", 0.5, 0.5, 0.5, 0.5, 0.5);
                    drive("Backward", 0.745, 0.3, 0.3, 0.3, 0.3);
                    drive("Left", 1.1, 0.5, 0.5 ,0.5, 0.5);
                    drive("Backward", 0.75, 0.5, 0.5, 0.5, 0.5);
                    driveStop();
                    startdriveflag = false;
                    break;
                }//drop paxel

                if (label == "Red Middle" && startdriveflag) {

                    drive("Backward", 0.045, 0.5, 0.5, 0.5, 0.5);
                    drive("TurnLeft", 0.725, 0.5, 0.5, 0.5, 0.5);
                    drive("Backward", 2.425, 0.3, 0.3, 0.3, 0.3);
                    driveStop();

                    drive("Left", 1.225, 0.5, 0.5 ,0.5, 0.5);
                    drive("Backward", 0.5, 0.5, 0.5, 0.5, 0.5);
                    driveStop();
                    startdriveflag = false;
                    break;
                }
            }
        }
        

                }

        }

