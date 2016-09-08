TITLE Mod file for component: Component(id=GABA_syn type=expTwoSynapse)

COMMENT

    This NEURON file has been generated by org.neuroml.export (see https://github.com/NeuroML/org.neuroml.export)
         org.neuroml.export  v1.5.0
         org.neuroml.model   v1.5.0
         jLEMS               v0.9.8.7

ENDCOMMENT

NEURON {
    POINT_PROCESS GABA_syn
    RANGE tauRise                           : parameter
    RANGE tauDecay                          : parameter
    RANGE peakTime                          : parameter
    RANGE waveformFactor                    : parameter
    RANGE gbase                             : parameter
    RANGE erev                              : parameter
    
    RANGE g                                 : exposure
    
    RANGE i                                 : exposure
    
    
    NONSPECIFIC_CURRENT i 
    
}

UNITS {
    
    (nA) = (nanoamp)
    (uA) = (microamp)
    (mA) = (milliamp)
    (A) = (amp)
    (mV) = (millivolt)
    (mS) = (millisiemens)
    (uS) = (microsiemens)
    (molar) = (1/liter)
    (kHz) = (kilohertz)
    (mM) = (millimolar)
    (um) = (micrometer)
    (umol) = (micromole)
    (S) = (siemens)
    
}

PARAMETER {
    
    tauRise = 5 (ms)
    tauDecay = 12 (ms)
    peakTime = 7.5040174 (ms)
    waveformFactor = 3.20378 
    gbase = 5.9999997E-4 (uS)
    erev = -80 (mV)
}

ASSIGNED {
    ? Standard Assigned variables with baseSynapse
    v (mV)
    celsius (degC)
    temperature (K)
    
    g (uS)                                 : derived variable
    
    i (nA)                                 : derived variable
    rate_A (/ms)
    rate_B (/ms)
    
}

STATE {
    A  
    B  
    
}

INITIAL {
    temperature = celsius + 273.15
    
    rates()
    rates() ? To ensure correct initialisation.
    
    A = 0
    
    B = 0
    
}

BREAKPOINT {
    
    SOLVE states METHOD cnexp
    
    
}

NET_RECEIVE(weight) {
    ?state_discontinuity(A, A  + (weight *   waveformFactor  )) : From GABA_syn
    A = A  + (weight *   waveformFactor  ) : From GABA_syn
    ?state_discontinuity(B, B  + (weight *   waveformFactor  )) : From GABA_syn
    B = B  + (weight *   waveformFactor  ) : From GABA_syn
    
}

DERIVATIVE states {
    rates()
    A' = rate_A 
    B' = rate_B 
    
}

PROCEDURE rates() {
    
    g = gbase  * (  B   -   A  ) ? evaluable
    i = -1 * g  * (  erev   - v) ? evaluable
    rate_B = -  B   /  tauDecay ? Note units of all quantities used here need to be consistent!
    rate_A = -  A   /  tauRise ? Note units of all quantities used here need to be consistent!
    
     
    
}

