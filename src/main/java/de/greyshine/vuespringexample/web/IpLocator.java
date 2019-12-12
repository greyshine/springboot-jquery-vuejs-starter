package de.greyshine.vuespringexample.web;

/**
 * Check for some implementation ideas: https://stackoverflow.com/q/6006915/845117
 */
@FunctionalInterface
public interface IpLocator {

	/**
	 * @param ip
	 * @return a location for given IP / information, <tt>null</tt> if none available.
	 */
	Location get(String ip, long millisToWait);
	
	interface Location {
		double latitude();
		double longitude();
	}

	public static Location buildLocation(final Double lat, final Double lon, Long runtime) {
		return lat == null || lon == null ? null : new Location() {
			@Override
			public double longitude() {
				return lat;
			}
			@Override
			public double latitude() {
				return lon;
			}
			@Override
			public String toString() {
				return Location.class.getSimpleName() +" [latitude="+ lat +", longitude="+ lon +", runtime="+ (runtime==null||runtime<0?"?":runtime+"ms") +"]";
			}
		};
		
	}
}
