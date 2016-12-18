classdef dW < handle
  methods 
    function [] = dW_a1(this, name)
      disp(['Variable Get', name]);
    end
    function [] = dW_a2(this)
      disp('Plus operation');
    end
  end
end