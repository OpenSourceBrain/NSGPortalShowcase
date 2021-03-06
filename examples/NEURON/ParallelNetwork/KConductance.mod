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

TITLE Channel: KConductance

COMMENT
    Simple example of K conductance in squid giant axon. Based on channel from Hodgkin and Huxley 1952
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
      

    SUFFIX KConductance
    USEION k READ ek WRITE ik VALENCE 1 ? reversal potential of ion is read, outgoing current is written
            
    RANGE gmax, gion
    
    RANGE ninf, ntau
}

PARAMETER { 
      

    gmax = 0.036 (S/cm2) ? default value, should be overwritten when conductance placed on cell
    
}



ASSIGNED {
      

    v (mV)
    
    celsius (degC)
    
    ? Reversal potential of k
    ek (mV)
    ? The outward flow of ion: k calculated by rate equations...
    ik (mA/cm2)
            
    
    gion (S/cm2)
    ninf
    ntau (ms)
    
}

BREAKPOINT { 
                        
    SOLVE states METHOD cnexp
         

    gion = gmax*((1*n)^4)
    ik = gion*(v - ek)
                

}



INITIAL {
    ek = -77.0
        
    rates(v)
    n = ninf
        
    
}
    
STATE {
    n
    
}

DERIVATIVE states {
    rates(v)
    n' = (ninf - n)/ntau
    
}

PROCEDURE rates(v(mV)) {  
    
    ? Note: not all of these may be used, depending on the form of rate equations
    LOCAL  alpha, beta, tau, inf, gamma, zeta, temp_adj_n, A_alpha_n, k_alpha_n, d_alpha_n, A_beta_n, k_beta_n, d_beta_n
        
    TABLE ninf, ntau
 DEPEND celsius
 FROM -100 TO 100 WITH 400
    
    
    UNITSOFF
    temp_adj_n = 1
    
        
    ?      ***  Adding rate equations for gate: n  ***
        
    ? Found a parameterised form of rate equation for alpha, using expression: A*(k*(v-d)) / (1 - exp(-(k*(v-d))))
    A_alpha_n = 0.1
    k_alpha_n = 0.1
    d_alpha_n = -55
     
    
    alpha = A_alpha_n * vtrap((v - d_alpha_n), (1/k_alpha_n))
    
    
    ? Found a parameterised form of rate equation for beta, using expression: A*exp(k*(v-d))
    A_beta_n = 0.125
    k_beta_n = -0.0125
    d_beta_n = -65
     
    
    beta = A_beta_n * exp((v - d_beta_n) * k_beta_n)
    
    ntau = 1/(temp_adj_n*(alpha + beta))
    ninf = alpha/(alpha + beta)
          
       
    
    ?     *** Finished rate equations for gate: n ***
    
             

}


? Function to assist with parameterised expressions of type linoid/exp_linear

FUNCTION vtrap(VminV0, B) {
    if (fabs(VminV0/B) < 1e-6) {
    vtrap = (1 + VminV0/B/2)
}else{
    vtrap = (VminV0 / B) /(1 - exp((-1 *VminV0)/B))
    }
}

UNITSON


