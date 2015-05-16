var song = function(bpm) {

  // the bpm is set to 128 if no bpm is passed in
  bpm = bpm || 128;

  // the four on the floor pattern; each step is an index, the value at each index represents the patterns played on that step
  var fourOnTheFloor = [
    ['bassDrum'],
    [],
    ['hiHat'],
    [],
    ['bassDrum', 'snareDrum'],
    [],
    ['hiHat'],
    []
  ];

  var userPatterns = Array.prototype.slice.call(arguments, 1)[0];
  // if there are user patterns
  if(userPatterns) {
  // add the user generated patterns to the existing fourOnTheFloor pattern
    for(pattern in userPatterns) {
      var idx = parseInt(pattern);
      fourOnTheFloor[idx] = fourOnTheFloor[idx] || [];
      fourOnTheFloor[idx] = fourOnTheFloor[idx].concat(userPatterns[pattern]);
    }
  } 

  for(var i = 0; i < fourOnTheFloor.length; i++) {
  // if a step is undefined (a user has added a non-sequential step pattern) or 
  // if one of the original steps remains without a pattern
    if(fourOnTheFloor[i] === undefined || fourOnTheFloor[i].length === 0) {
      // set the pattern for that step to a 'rest'
      fourOnTheFloor[i] = 'rest';
    } else {
      // otherwise concatenate multiple patterns on a single step as this will not impact single patterns on a step
      // ex: ['bassDrum', 'snareDrum'] --> 'bassDrum+snareDrum'
      // ex: [hiHat] --> 'hiHat'
      fourOnTheFloor[i] = fourOnTheFloor[i].join('+');
    }
  }

  // a function that plays each sound in our pattern at the appropriate tempo
  var play = function(patternLength) {
    // must maintain count for delayed array value lookup
    var count = 0;
    for(var j = 0; j < patternLength; j++) {
      // interval is the index times the step per second
      var interval = j * (60 / bpm * 4 / 8 * 1000); 
      setTimeout(function(){
        // console.log each pattern in time
        console.log(fourOnTheFloor[count]);
        count++; 
      }, interval);
    }
  }

  // invoke play function
  play(fourOnTheFloor.length);

};

// a user can add patterns by passing in an object with the step as the key and the patterns in an array as the value 
song(60, {16: ['bang', 'boom'], 15: ['boop']} ); // song(); also works with no parameters
