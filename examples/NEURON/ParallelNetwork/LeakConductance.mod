COMMENT

   **************************************************
   File generated by: neuroConstruct v1.7.0 
   **************************************************


ENDCOMMENT


?  This is a NEURON mod file generated from a ChannelML file

?  Unit system of original ChannelML file: Physiological Units

COMMENT
    ChannelML file containing a single Channel description
ENDCOMMENT

TITLE Channel: LeakConductance

COMMENT
    Simple example of a leak/passive conductance. 
ENDCOMMENT


UNITS {
    (mA) = (milliamp)
    (mV) = (millivolt)
    (S) = (siemens)
    (um) = (micrometer)
    (molar) = (1/liter)
    (mM) = (millimolar)
    (l) = (liter)
}


    
NEURON {
      

    SUFFIX LeakConductance
    ? A non specific current is present
    RANGE e
    NONSPECIFIC_CURRENT i
    
    RANGE gmax, gion
    
}

PARAMETER { 
      

    gmax = 0.0003 (S/cm2)  ? default value, should be overwritten when conductance placed on cell
    
    e = -60 (mV) ? default value, should be overwritten when conductance placed on cell
    
}



ASSIGNED {
      

    v (mV)
        
    i (mA/cm2)
        
}

BREAKPOINT { 
    i = gmax*(v - e) 
        

}

