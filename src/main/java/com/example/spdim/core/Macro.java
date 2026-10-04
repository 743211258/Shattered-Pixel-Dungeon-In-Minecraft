package com.example.spdim.core;

public class Macro {
	public enum STATE {
		IDLE,
		USING,
		COOLDOWN
	}

	public static final float BLAST_WAVE_DAMAGE_MULTIPLIER = 1.0F;

	public static final float BLAST_WAVE_KNOCKBACK_MULTIPLIER = 0.1F;
	
	public static final int CHALICE_OF_BLOOD_ABSORPTION_EFFECT_DURATION = 3600;

	public static final float CHALICE_OF_BLOOD_DAMAGE_ENSUING_AFTER_ABSORPTION = 10.0F;
	
	public static final float CHALICE_OF_BLOOD_FINAL_ABSORPTION = 190.0F;
	
	public static final int CHALICE_OF_BLOOD_FREEZE_EFFECT_DURATION = 200;

	public static final int CHALICE_OF_BLOOD_JUMP_EFFECT_DURATION = 3600;

	public static final int CHALICE_OF_BLOOD_NIGHT_VISION_EFFECT_DURATION = 3600;

	public static final int CHALICE_OF_BLOOD_NO_HEALING_EFFECT_DURATION = 6000;
	
	public static final float CHALICE_OF_BLOOD_ON_USE_DAMAGE = 19.0F;
	
	public static final int CHALICE_OF_BLOOD_RESISTANCE_EFFECT_DURATION = 3600;

	public static final int CHALICE_OF_BLOOD_STRENGTH_EFFECT_DURATION = 3600;

	public static final int CHALICE_OF_BLOOD_SWITFNESS_EFFECT_DURATION = 3600;

	public static final float CHALICE_OF_BLOOD_TEMPORARY_ABSORPTION = 200.0F;
	
	public static final int DRIED_ROSE_CONTROL_CONTROL_RANGE = 100;

	public static final int DRIED_ROSE_COOLDOWN  = 2400;

	public static final int DRIED_ROSE_TELEPORT_CONTROL_RANGE = 200;

	public static final int MASTER_THIEVES_ARMBAND_COOLDOWN = 6000;

	public static final int MASTER_THIEVES_ARMBAND_CONTROL_RANGE = 6000;

	public static final String MYMODNETWORK_PROTOCOL_VERSION = "1";

	public static final float TAUNT_CHARGE_PER_TICK = 0.025F;

	public static final float TAUNT_COST_PER_TICK = 0.1F;
	
	public static final float TAUNT_EFFECT_RADIUS = 16.0F;
	
	public static final float TIMEKEEPERS_HOURGLASS_CONTROL_RANGE = 50.0F;
	
	public static final int TIMEKEEPERS_HOURGLASS_COOLDOWN = 900;

	public static final int TIMEKEEPERS_FREEZE_OTHER_DURATION = 100;
	
	public static final int TIMEKEEPERS_FREEZE_SELF_DURATION = 200;

	public static final float WAND_OF_BLAST_WAVE_EXPLODE_RADIUS = 5.0F;
	
	public static final float WAND_OF_BLAST_WAVE_SPEED = 3.0F;
	
	public static final double WAND_OF_FIRE_BLAST_CONE_RANGE_HEIGHT = 20.0D;
	
	public static final double WAND_OF_FIRE_BLAST_CONE_RANGE_RADIUS = 10.0D;
	
	public static final double WAND_OF_FIRE_BLAST_CONE_RANGE_STEP_HEIGHT = 1.0D;
	
	public static final double WAND_OF_FIRE_BLAST_CONE_RANGE_STEP_RADIUS = WAND_OF_FIRE_BLAST_CONE_RANGE_RADIUS / WAND_OF_FIRE_BLAST_CONE_RANGE_HEIGHT;

	public static final double WAND_OF_REGROWTH_CONE_RANGE_HEIGHT = 20.0D;
	
	public static final double WAND_OF_REGROWTH_CONE_RANGE_RADIUS = 10.0D;
	
	public static final double WAND_OF_REGROWTH_CONE_RANGE_STEP_HEIGHT = 1.0D;
	
	public static final double WAND_OF_REGROWTH_CONE_RANGE_STEP_RADIUS = WAND_OF_REGROWTH_CONE_RANGE_RADIUS / WAND_OF_REGROWTH_CONE_RANGE_HEIGHT;
}
