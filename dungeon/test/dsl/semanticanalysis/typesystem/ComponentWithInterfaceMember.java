package dsl.semanticanalysis.typesystem;

import dsl.annotation.DSLType;
import dsl.annotation.DSLTypeMember;

@DSLType
public class ComponentWithInterfaceMember {
  private @DSLTypeMember ITestInterface interfaceMember;
}
