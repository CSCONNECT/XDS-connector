package org.net4care.xdsconnector.Utilities;

public class CodedValue {
  private String code;
  private String codeSystem;
  private String codeSystemName;
  private String displayName;
  
  protected CodedValue() {}

  public CodedValue(String code, String codeSystem, String displayName, String codeSystemName) {
    this.code = code;
    this.codeSystem = codeSystem;
    this.displayName = displayName;
    this.codeSystemName = codeSystemName;
  }
  
  public CodedValue(String code, String codeSystem, String codeSystemName) {
    this.code = code;
    this.codeSystem = codeSystem;
    this.codeSystemName = codeSystemName;
  }

  public String getCode() {
    return code;
  }

  public String getCodeSystem() {
    return codeSystem;
  }

  public String getCodeSystemName() {
    return codeSystemName;
  }

  public String getDisplayName() {
    return displayName;
  }

  protected void setCode(String code) {
    this.code = code;
  }

  protected void setCodeSystem(String codeSystem) {
    this.codeSystem = codeSystem;
  }

  protected void setCodeSystemName(String codeSystemName) {
    this.codeSystemName = codeSystemName;
  }

  protected void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * 
   * Builder class
   *
   */
  public static class CodedValueBuilder {
    private CodedValue codedValue;
    
    public CodedValueBuilder() {
      codedValue = new CodedValue();
    }
    
    public CodedValueBuilder setCode(String code) {
      codedValue.setCode(code);
      return this;
    }
    public CodedValueBuilder setCodeSystem(String codeSystem) {
      codedValue.setCodeSystem(codeSystem);
      return this;
    }
    public CodedValueBuilder setDisplayName(String displayName) {
      codedValue.setDisplayName(displayName);
      return this;
    }
    public CodedValueBuilder setCodeSystemName(String codeSystemName) {
      codedValue.setCodeSystemName(codeSystemName);
      return this;
    }
    public CodedValue build() {
      return codedValue;
    }
  }

}
