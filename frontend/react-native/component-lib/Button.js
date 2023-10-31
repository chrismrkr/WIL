/*  Button.js
**  Customized Button on which programmer can invoke event
*/
import { Pressable, Text, StyleSheet, ActivityIndicator } from "react-native";

export const ButtonTypes = {
    PRIMARY: 'PRIMARY',
    DANGER: 'DANGER',
};

const Button = ({title, onPressEvent, disabled, isLoading, buttonType}) => {
    return (
        <Pressable onPress={()=>onPressEvent()}
        style={({pressed}) => 
            [   styles.container, 
                disabled && {backgroundColor: colors[buttonType].LIGHT},
                pressed && {backgroundColor: colors[buttonType].DARK},
                {backgroundColor: colors[buttonType].DEFAULT}
            ]}
        disabled={disabled}
        >
            {isLoading ? 
            (<ActivityIndicator size={'small'} color={GRAY.DEFAULT}/>) :
            (<Text style={styles.title}>{title}</Text>)}
        
        </Pressable>
    )
};

const colors = {
  WHITE: '#ffffff',
  BLACK: '#000000',
  PRIMARY: {
    DEFAULT: '#2563eb',
    LIGHT: '#93c5fd',
    DARK: '#1e3a8a',
  },
  GRAY: {
    DEFAULT: '#a3a3a3',
  },
  DANGER: {
    LGIHT: "#fca5a5",
    DEFAULT: "#dc2626",
    DARK: "#7f1d1d",
  }  
};

const styles = StyleSheet.create({
    container: {
        backgroundColor: colors[PRIMARY].LIGHT,
        borderRadius: 8,
        justifyContent: 'center',
        alignItems: 'center',
        paddingVertical: 20,
    },
    title: {
        color: colors[WHITE],
        fontSize: 16,
        fontWeight: '700',
        lineHeight: 20,
    },
})

export default Button;
