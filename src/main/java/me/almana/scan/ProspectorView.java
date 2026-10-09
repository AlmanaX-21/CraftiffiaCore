package me.almana.scan;

public enum ProspectorView {
   SURFACE("Surface", ScanMode.LARGE),
   BEDROCK("Bedrock", ScanMode.BEDROCK),
   FLUID_SOURCES("Fluid Sources", ScanMode.FLUID_BEDROCK);

   public final String label;
   public final ScanMode mode;

   private ProspectorView(String aLabel, ScanMode aMode) {
      this.label = aLabel;
      this.mode = aMode;
   }
}
